/* import React, { Component } from "react";
import "bootstrap/dist/css/bootstrap.min.css";


import Home from "./components/home.component";
import BoardUser from "./components/board-user.component";

class App extends Component {
  constructor(props) {
    super(props);
    this.logOut = this.logOut.bind(this);

    this.state = {
      currentUser: undefined,
    };
  }

  componentDidMount() {
    const user = AuthService.getCurrentUser();

    if (user) {
      this.setState({
        currentUser: user,
      });
    }
  }

  logOut() {
    AuthService.logout();
  }

  render() {
    const { currentUser } = this.state;

    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <Link to={"/"} className="navbar-brand">
            FlashDrive
          </Link>
          <div className="navbar-nav mr-auto">
            <li className="nav-item">
              <Link to={"/home"} className="nav-link">
                Home
              </Link>
            </li>

            {currentUser && (
              <li className="nav-item">
                <Link to={"/user"} className="nav-link">
                  User
                </Link>
              </li>
            )}
          </div>

          {currentUser ? (
            <div className="navbar-nav ml-auto">
              <li className="nav-item">
                <Link to={"/profile"} className="nav-link">
                  {currentUser.username}
                </Link>
              </li>
              <li className="nav-item">
                <a href="/login" className="nav-link" onClick={this.logOut}>
                  LogOut
                </a>
              </li>
            </div>
          ) : (
            <div className="navbar-nav ml-auto">
              <li className="nav-item">
                <Link to={"/login"} className="nav-link">
                  Login
                </Link>
              </li>

              <li className="nav-item">
                <Link to={"/register"} className="nav-link">
                  Sign Up
                </Link>
              </li>
            </div>
          )}
        </nav>

        <div className="container mt-3">
          <Switch>
            <Route exact path={["/", "/home"]} component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/register" component={Register} />
            <Route exact path="/profile" component={Profile} />
            <Route path="/user" component={BoardUser} />
          </Switch>
        </div>
      </div>
    );
  }
}

export default App; */

import React, { useEffect } from 'react';
import Home from './components/pages/Home';
import Navbar from './components/pages/layouts/Navbar';
import Loginn from "./components/login.component";
import Login from "./components/Login";
import Register from "./components/register.component";
import Profile from "./components/profile.component";
import { Switch, Route } from "react-router-dom";
import "./App.css";

export default function App() {

  useEffect(() => {
    /* Append external Scripts to the DOM*/
      //An array of assets
      let scripts = [
        { src: "assets/js/jquery.min.js" },
        { src: "assets/js/bootstrap.min.js" },
        { src: "assets/js/jquery.easing.min.js" },
        { src: "assets/js/swiper.min.js" },
        { src: "assets/js/jquery.magnific-popup.js" },
        { src: "assets/js/scripts.js" }
      ]
      //Append the script element on each iteration
      scripts.forEach(item => { 
          const script = document.createElement("script")
          script.src = item.src
          script.async = false
          document.body.appendChild(script)
      })
     
  })

  return (
    <div className="App">
        <Navbar />
        <div className="container mt-3">
          <Switch>
            <Route exact path={["/", "/home"]} component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/register" component={Register} />
            <Route exact path="/profile" component={Profile} />
            {/* <Route path="/user" component={BoardUser} /> */}
          </Switch>
        </div>
    </div>
  )
}
