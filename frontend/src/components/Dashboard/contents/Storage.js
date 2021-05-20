import React from 'react'
import StorageHeader from './StorageHeader'

export default function Storage() {
    return (
        <div>
            <div className="content-panel">
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

            </div>
        </div>
    )
}
