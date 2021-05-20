import React from 'react'

export default function StorageHeader() {
    return (
        <div>
            <div className="content-header-wrapper">
                <h2 className="title">My FlashDrive</h2>
                <div className="actions">
                    <label class="btn btn-success">
                        <input id="my-file-selector" type="file" class="d-none" />
                        <i className="fa fa-plus"></i> Upload new file
                    </label>
                </div>
            </div>
            <div className="content-utilities">
                <div className="page-nav">
                    <span className="indicator">View:</span>
                    <div className="btn-group" role="group">
                        <button className="active btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="Grid View" id="drive-grid-toggle"><i className="fa fa-th-large"></i></button>
                        <button className="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="List View" id="drive-list-toggle"><i className="fa fa-list-ul"></i></button>
                    </div>
                </div>
                <div className="actions">
                    <div className="btn-group">
                        <button className="btn btn-default dropdown-toggle" data-toggle="dropdown" type="button" aria-expanded="false">All Items <span className="caret"></span></button>
                        <ul className="dropdown-menu">
                            <li><a href="#"><i className="fa fa-file"></i> Documents</a></li>
                            <li><a href="#"><i className="fa fa-file-image-o"></i> Images</a></li>
                            <li><a href="#"><i className="fa fa-file-video-o"></i> Media Files</a></li>
                            <li><a href="#"><i className="fa fa-folder"></i> Folders</a></li>
                        </ul>
                    </div>
                    <div className="btn-group">
                        <button className="btn btn-default dropdown-toggle" data-toggle="dropdown" type="button" aria-expanded="false"><i className="fa fa-filter"></i> Sorting <span className="caret"></span></button>
                        <ul className="dropdown-menu">
                            <li><a href="#">Newest first</a></li>
                            <li><a href="#">Oldest first</a></li>
                        </ul>
                    </div>
                    <div className="btn-group" role="group">
                        <button type="button" className="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="Refresh"><i className="fa fa-refresh"></i></button>
                        <button type="button" className="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="Archive"><i className="fa fa-archive"></i></button>

                        <button type="button" className="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="Report spam"><i className="fa fa-exclamation-triangle"></i></button>
                        <button type="button" className="btn btn-default" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="Delete"><i className="fa fa-trash-o"></i></button>
                    </div>
                </div>
            </div>
        </div>
    )
}
