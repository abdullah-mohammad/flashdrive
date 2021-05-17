import React from 'react'

export default function Home() {

    return (
        <div id="home-bg">
            {/* Header */}
            <header id="header" className="header">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-6">
                            <div className="text-container">
                                <h1 className="h1-large">Store and manage your data in only one place</h1>
                                <p className="p-large">Start uploading and saving your data on a secure place. With FlashDrive you can access your data in the cloud at any time on demand and manage them as you want.</p>
                                <a className="btn-solid-lg" href="#your-link"><i className="fab fa fa-box-open"></i>Our services</a>
                                <a className="btn-solid-lg secondary" href="#your-link" style={{marginBottom: 0}}><i className="fab fa fa-sign-in-alt"></i>Sign in</a>
                            </div> {/* end of text-container */}
                        </div> {/* end of col */}
                        <div className="col-lg-6 d-none d-sm-block">
                            <div className="image-container">
                                <img className="img-fluid" src="assets/images/computer.png" alt="alternative" />
                            </div> {/* end of image-container */}
                        </div> {/* end of col */}
                    </div> {/* end of row */}
                </div> {/* end of container */}
            </header>
            {/* end of header */}

        </div>
    )
}
