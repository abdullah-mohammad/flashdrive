import React, { useEffect, useState } from 'react'
import { Link, Route, Switch } from 'react-router-dom'
import authService from '../../services/auth.service'
import Storage from './contents/Storage'
import SpeechToText from './contents/speech-to-text'
import { DASHBOARD_BASE_URL } from './dashboardBaseUrl'

export default function Dashboard() {

    const [currentUser, setCurrentUser] = useState(undefined)

    useEffect(() => {
        const user = authService.getCurrentUser();
        if (user) {
            setCurrentUser(user)
        }
    }, [])


    return (
        <div className="container">
            <div className="view-account">
                <section className="module">
                    <div className="module-inner">

                        {/* Sidebar */}
                        <div className="side-bar">
                            <div className="user-info">
                                <img className="img-profile img-circle img-responsive center-block" src="https://bootdey.com/img/Content/avatar/avatar6.png" alt="" />
                                <ul className="meta list list-unstyled">
                                    <li className="name">Rebecca Sanders
                                        <label className="label label-info">UX Designer</label>
                                    </li>
                                    <li className="email"><a href="#">Rebecca.S@website.com</a></li>
                                    <li className="activity">Last logged in: Today at 2:18pm</li>
                                </ul>
                            </div>
                            <nav className="side-menu">
                                <ul className="nav">
                                    <li><Link to="/profile"><span className="fa fa-user"></span> Profile</Link></li>
                                    <li><Link to="/home"><span className="fa fa-cog"></span> Home</Link></li>
                                    <li className="active"><Link to={`${DASHBOARD_BASE_URL}/storage`}><span className="fa fa-th"></span> Storage</Link></li>
                                    <li><Link to={`${DASHBOARD_BASE_URL}/speech-to-text`}><span className="fa fa-envelope"></span> Speech-To-Text</Link></li>
                                </ul>
                            </nav>
                        </div>
                        {/* ./Sidebar */}

                        <Switch>
                            <Route exact path={`${DASHBOARD_BASE_URL}/storage`} component={Storage} />
                            <Route exact path={`${DASHBOARD_BASE_URL}/speech-to-text`} component={SpeechToText} />
                        </Switch>


                    </div>
                </section>
            </div>
        </div>
    )
}
