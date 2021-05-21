import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import StorageHeader from './StorageHeader'

import { 
    FileActionData, FileAction, FileActionHandler, 
    FileData,
    ChonkyActions, ChonkyIconName, defineFileAction, FullFileBrowser, ChonkyFileActionData, FileHelper, FileArray } from 'chonky';

import { setChonkyDefaults } from 'chonky';
import { ChonkyIconFA } from 'chonky-icon-fontawesome';

import DemoFsMap from './demos/demo.fs_map.json';
import { showActionNotification } from './util';
import { useDispatch, useSelector } from 'react-redux';
import { getAllFiles } from '../../../redux/file/fileActions';
import authService from '../../../services/auth.service';

setChonkyDefaults({ iconComponent: ChonkyIconFA,  });

// We define a custom interface for file data because we want to add some custom fields
// to Chonky's built-in `FileData` interface.
interface CustomFileData extends FileData {
    parentId?: string;
    childrenIds?: string[];
}
interface CustomFileMap {
    [fileId: string]: CustomFileData;
}

// Helper method to attach our custom TypeScript types to the imported JSON file map.
const prepareCustomFileMap = () => {
    const baseFileMap = (DemoFsMap.fileMap as unknown) as CustomFileMap;
    const rootFolderId = DemoFsMap.rootFolderId;
    return { baseFileMap, rootFolderId };
};

// Hook that sets up our file map and defines functions used to mutate - `deleteFiles`,
// `moveFiles`, and so on.
const useCustomFileMap = () => {
    const { baseFileMap, rootFolderId } = useMemo(prepareCustomFileMap, []);

    // Setup the React state for our file map and the current folder.
    const [fileMap, setFileMap] = useState(baseFileMap);
    const [currentFolderId, setCurrentFolderId] = useState(rootFolderId);

    // Setup the function used to reset our file map to its initial value. Note that
    // here and below we will always use `useCallback` hook for our functions - this is
    // a crucial React performance optimization, read more about it here:
    // https://reactjs.org/docs/hooks-reference.html#usecallback
    const resetFileMap = useCallback(() => {
        setFileMap(baseFileMap);
        setCurrentFolderId(rootFolderId);
    }, [baseFileMap, rootFolderId]);

    // Setup logic to listen to changes in current folder ID without having to update
    // `useCallback` hooks. Read more about it here:
    // https://reactjs.org/docs/hooks-faq.html#is-there-something-like-instance-variables
    const currentFolderIdRef = useRef(currentFolderId);
    useEffect(() => {
        currentFolderIdRef.current = currentFolderId;
    }, [currentFolderId]);

    // Function that will be called when user deletes files either using the toolbar
    // button or `Delete` key.
    const deleteFiles = useCallback((files: CustomFileData[]) => {
        // We use the so-called "functional update" to set the new file map. This
        // lets us access the current file map value without having to track it
        // explicitly. Read more about it here:
        // https://reactjs.org/docs/hooks-reference.html#functional-updates
        setFileMap((currentFileMap) => {
            // Create a copy of the file map to make sure we don't mutate it.
            const newFileMap = { ...currentFileMap };

            files.forEach((file) => {
                // Delete file from the file map.
                delete newFileMap[file.id];

                // Update the parent folder to make sure it doesn't try to load the
                // file we just deleted.
                if (file.parentId) {
                    const parent = newFileMap[file.parentId]!;
                    const newChildrenIds = parent.childrenIds!.filter(
                        (id) => id !== file.id
                    );
                    newFileMap[file.parentId] = {
                        ...parent,
                        childrenIds: newChildrenIds,
                        childrenCount: newChildrenIds.length,
                    };
                }
            });

            return newFileMap;
        });
    }, []);

    // Function that will be called when files are moved from one folder to another
    // using drag & drop.
    const moveFiles = useCallback(
        (
            files: CustomFileData[],
            source: CustomFileData,
            destination: CustomFileData
        ) => {
            setFileMap((currentFileMap) => {
                const newFileMap = { ...currentFileMap };
                const moveFileIds = new Set(files.map((f) => f.id));

                // Delete files from their source folder.
                const newSourceChildrenIds = source.childrenIds!.filter(
                    (id) => !moveFileIds.has(id)
                );
                newFileMap[source.id] = {
                    ...source,
                    childrenIds: newSourceChildrenIds,
                    childrenCount: newSourceChildrenIds.length,
                };

                // Add the files to their destination folder.
                const newDestinationChildrenIds = [
                    ...destination.childrenIds!,
                    ...files.map((f) => f.id),
                ];
                newFileMap[destination.id] = {
                    ...destination,
                    childrenIds: newDestinationChildrenIds,
                    childrenCount: newDestinationChildrenIds.length,
                };

                // Finally, update the parent folder ID on the files from source folder
                // ID to the destination folder ID.
                files.forEach((file) => {
                    newFileMap[file.id] = {
                        ...file,
                        parentId: destination.id,
                    };
                });

                return newFileMap;
            });
        },
        []
    );

    // Function that will be called when user creates a new folder using the toolbar
    // button. That that we use incremental integer IDs for new folder, but this is
    // not a good practice in production! Instead, you should use something like UUIDs
    // or MD5 hashes for file paths.
    const idCounter = useRef(0);
    const createFolder = useCallback((folderName: string) => {
        setFileMap((currentFileMap) => {
            const newFileMap = { ...currentFileMap };

            // Create the new folder
            const newFolderId = `new-folder-${idCounter.current++}`;
            newFileMap[newFolderId] = {
                id: newFolderId,
                name: folderName,
                isDir: true,
                modDate: new Date(),
                parentId: currentFolderIdRef.current,
                childrenIds: [],
                childrenCount: 0,
            };

            // Update parent folder to reference the new folder.
            const parent = newFileMap[currentFolderIdRef.current];
            newFileMap[currentFolderIdRef.current] = {
                ...parent,
                childrenIds: [...parent.childrenIds!, newFolderId],
            };

            return newFileMap;
        });
    }, []);

    return {
        fileMap,
        currentFolderId,
        setCurrentFolderId,
        resetFileMap,
        deleteFiles,
        moveFiles,
        createFolder,
    };
};


export const useFiles = (
    fileMap: CustomFileMap,
    currentFolderId: string
): FileArray => {
    return useMemo(() => {
        const currentFolder = fileMap[currentFolderId];
        const childrenIds = currentFolder.childrenIds!;
        const files = childrenIds.map((fileId: string) => fileMap[fileId]);
        return files;
    }, [currentFolderId, fileMap]);
};

export const useFolderChain = (
    fileMap: CustomFileMap,
    currentFolderId: string
): FileArray => {
    return useMemo(() => {
        const currentFolder = fileMap[currentFolderId];

        const folderChain = [currentFolder];

        let parentId = currentFolder.parentId;
        while (parentId) {
            const parentFile = fileMap[parentId];
            if (parentFile) {
                folderChain.unshift(parentFile);
                parentId = parentFile.parentId;
            } else {
                break;
            }
        }

        return folderChain;
    }, [currentFolderId, fileMap]);
};

export const useFileActionHandler = (
    setCurrentFolderId: (folderId: string) => void,
    deleteFiles: (files: CustomFileData[]) => void,
    moveFiles: (files: FileData[], source: FileData, destination: FileData) => void,
    createFolder: (folderName: string) => void
) => {
    return useCallback(
        (data: ChonkyFileActionData) => {
            if (data.id === ChonkyActions.OpenFiles.id) {
                const { targetFile, files } = data.payload;
                const fileToOpen = targetFile ?? files[0];
                if (fileToOpen && FileHelper.isDirectory(fileToOpen)) {
                    setCurrentFolderId(fileToOpen.id);
                    return;
                }
            } else if (data.id === ChonkyActions.DeleteFiles.id) {
                deleteFiles(data.state.selectedFilesForAction!);
            } else if (data.id === ChonkyActions.MoveFiles.id) {
                moveFiles(
                    data.payload.files,
                    data.payload.source!,
                    data.payload.destination
                );
            } else if (data.id === ChonkyActions.CreateFolder.id) {
                const folderName = prompt('Provide the name for your new folder:');
                if (folderName) createFolder(folderName);
            }

            showActionNotification(data);
        },
        [createFolder, deleteFiles, moveFiles, setCurrentFolderId]
    );
};


export default function Storage() {

    const {
        fileMap,
        currentFolderId,
        setCurrentFolderId,
        resetFileMap,
        deleteFiles,
        moveFiles,
        createFolder,
    } = useCustomFileMap();
    const files = useFiles(fileMap, currentFolderId);
    const folderChain = useFolderChain(fileMap, currentFolderId);
    const handleFileAction = useFileActionHandler(
        setCurrentFolderId,
        deleteFiles,
        moveFiles,
        createFolder
    );
    const fileActions = useMemo(
        () => [ChonkyActions.CreateFolder, ChonkyActions.DeleteFiles],
        []
    );
    const thumbnailGenerator = useCallback(
        (file: FileData) =>
            file.thumbnailUrl ? `https://chonky.io${file.thumbnailUrl}` : null,
        []
    );


    /* const folderChain = [
        { id: 'xcv', name: 'root', isDir: true },
        null, // Will show loading placeholder
        { id: 'fgh', name: 'My Documents' },
    ];

    const [files, setFiles] = useState([
        { id: 'lht', name: 'Projects', isDir: true, color: '#57c6f2' },
        {
            id: 'mcd',
            name: 'chonky-sphere-v2.png',
            thumbnailUrl: 'https://chonky.io/chonky-sphere-v2.png',
            size: 5000,
            modDate: '20.05.2021',
            color: 'whitesmoke'
        },
        { id: 'file2', name: 'myAudioFile.mp4'},
        { id: 'file3', name: 'file.pdf' },
        null,
        { id: 'file4', name: 'file4.zip' },
    ])

    const myAction = defineFileAction({
        id: 'my_action',
        button: {
            name: 'Run My Action',
            toolbar: true,
            contextMenu: true,
            icon: ChonkyIconName.toggleOn, // <----
        },
    });


    const handleAction = React.useCallback<FileActionHandler>((data) => {
        alert('DATA:', data);
        if (data.id === ChonkyActions.OpenFileContextMenu.id) {
            console.log('You want to create a folder:', data);
        } else if (data.id === ChonkyActions.UploadFiles.id) {
            console.log('You want to upload a file:', data);
        }
    }, []); */
    /* const handleAction = React.useCallback(
        (action: FileAction, data: FileActionData) => {
            console.log('Action definition:', action);
            console.log('Action data:', data);
        },
        []
    ); */
    /* const handleAction = React.useCallback((data: FileActionData) => {
        console.log('DATA:', data);
        if (data.id === ChonkyActions.OpenFileContextMenu.id) {
            console.log('You want to create a folder:', data);
        } else if (data.id === ChonkyActions.UploadFiles.id) {
            console.log('You want to upload a file:', data);
        }
    }, []); */


    /* useEffect(() => {
        setTimeout(() => {
            setFiles([...files, { id: 'lht2', name: 'Project/Sub', isDir: true }])
        }, 2000);
    }, [files]) */


    const filess = useSelector(state => state)
    const dispatch = useDispatch()

    useEffect(() => {
        dispatch(getAllFiles(authService.getCurrentUser().username))

        setTimeout(() => {
            console.log("OOOO: ", filess)
        }, 2000);
    }, [])


    return (
        <div>
            <div className="content-panel" style={{height: '700px'}}>
                <StorageHeader />

                <div className="drive-wrapper drive-grid-view">
                    <div className="grid-items-wrapper">
                        <div className="drive-item module text-center">
                            <div className="drive-item-inner module-inner">
                                <div className="drive-item-title"><a href="#">UX Resource</a></div>
                                <div className="drive-item-thumb">
                                    <a href="#"><i className="fa fa-folder text-primary"></i></a>
                                </div>
                            </div>
                            <div className="drive-item-footer module-footer">
                                <ul className="utilities list-inline">
                                    <li className="mr-3 d-inline-block"><a href="#" data-toggle="tooltip" data-placement="top" title="" data-original-title="Download"><i className="fa fa-download"></i></a></li>
                                    <li className="mr-3 d-inline-block"><a href="#" data-toggle="tooltip" data-placement="top" title="" data-original-title="Delete"><i className="fa fa-trash"></i></a></li>
                                </ul>
                            </div>
                        </div>
                        <div className="drive-item module text-center">
                            <div className="drive-item-inner module-inner">
                                <div className="drive-item-title"><a href="#">Prototypes</a></div>
                                <div className="drive-item-thumb">
                                    <a href="#"><i className="fa fa-file-text-o text-primary"></i></a>
                                </div>
                            </div>
                            <div className="drive-item-footer module-footer">
                                <ul className="utilities list-inline">
                                    <li className="mr-3 d-inline-block"><a href="#" data-toggle="tooltip" data-placement="top" title="" data-original-title="Download"><i className="fa fa-download"></i></a></li>
                                    <li className="mr-3 d-inline-block"><a href="#" data-toggle="tooltip" data-placement="top" title="" data-original-title="Delete"><i className="fa fa-trash"></i></a></li>
                                    <li className="mr-3 d-inline-block"><a href="#" data-toggle="tooltip" data-placement="top" title="" data-original-title="Delete"><i className="fa fa-microphone"></i></a></li>
                                </ul>
                            </div>
                        </div>

                    </div>
                </div>


                <FullFileBrowser 
                    files={files} 
                    folderChain={folderChain} 
                    fileActions={fileActions} 
                    onFileAction={handleFileAction}
                    thumbnailGenerator={thumbnailGenerator}
                />




            </div>
        </div>
    )
}
