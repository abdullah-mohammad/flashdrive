import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import AuthService from "../../../services/auth.service";

export default function Navbar() {

    const [currentUser, setCurrentUser] = useState(undefined)

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user)
        }
    }, [])

    return (
        <div>
            {/* Navigation */}
            <nav className="navbar navbar-expand-lg fixed-top navbar-light">
                <div className="container">
                    {/* Text Logo - Use this if you don't have a graphic logo */}
                    {/* <a className="navbar-brand logo-text page-scroll" href="index.html">Pavo</a> */}

                    {/* Image Logo */}
                    <Link to="/" className="navbar-brand logo-image">
                        <img src="assets/images/logo.svg" alt="alternative" />
                        <span className="navbar-brand logo-text">FlashDrive</span>
                    </Link>

                    <button className="navbar-toggler p-0 border-0" type="button" data-toggle="offcanvas">
                        <span className="navbar-toggler-icon"></span>
                    </button>

                    <div className="navbar-collapse offcanvas-collapse" id="navbarsExampleDefault">
                        <ul className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to="/home" className="nav-link">
                                    Home
                                    <span className="sr-only">(current)</span>
                                </Link>
                            </li>
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" href="#" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Our Services</a>
                                <div className="dropdown-menu" aria-labelledby="dropdown01">
                                    <Link className="dropdown-item page-scroll" to="/flashdrive-storage">FlashDrive Storage</Link>
                                    <div className="dropdown-divider"></div>
                                    <Link className="dropdown-item page-scroll" to="/speech-to-text">Speech to Text</Link>
                                </div>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link page-scroll" to="/about">About</Link>
                            </li>
                            

                            {currentUser ? (
                                <>
                                    <li className="nav-item">
                                        <Link to="/profile" className="nav-link">
                                            Profile
                                        </Link>
                                    </li>
                                    <li className="nav-item">
                                        <Link to="/logout" className="nav-link">
                                            Log Out
                                        </Link>
                                    </li>
                                </>
                            ) : (
                                <>
                                    <li className="nav-item">
                                        {/* <a className="nav-link page-scroll" href="#features">Sign In</a> */}
                                        <Link to="/login" className="nav-link">
                                            Sign In
                                        </Link>
                                    </li>
                                    <li className="nav-item">
                                        <Link className="btn btn-sm btn-secondary btn-rounded" to="/register" style={{textDecoration:'none'}}>
                                            Create new account
                                        </Link>
                                    </li>
                                </>
                            )}

                        </ul>
                    </div> {/* end of navbar-collapse */}
                </div> {/* end of container */}
            </nav>
            {/* end of navigation */}



        </div>
    )
}
