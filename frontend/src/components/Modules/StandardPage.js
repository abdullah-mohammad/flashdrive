import React, { useEffect } from 'react';
import Home from './../pages/Home';
import Navbar from './../pages/layouts/Navbar';
import Login from "./../Login";
import Register from "./../Register";
import Profile from "./../profile.component";
import { Switch, Route } from "react-router-dom";

import "./StandardPage.css";
import "./../../SRC_assets/css/bootstrap.css"
import "./../../SRC_assets/css/fontawesome-all.css"
import "./../../SRC_assets/css/swiper.css"
import "./../../SRC_assets/css/magnific-popup.css"
import "./../../SRC_assets/css/styles.css"

export default function StandardPage() {

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


      /* let styles = [
        { href: "https://fonts.gstatic.com", rel: "stylesheet" },
        { href: "https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,600;0,700;1,400&display=swap", rel: "stylesheet" },
        { href: "/assets/css/bootstrap.css", rel: "stylesheet" },
        { href: "/assets/css/fontawesome-all.css", rel: "stylesheet" },
        { href: "/assets/css/swiper.css", rel: "stylesheet" },
        { href: "/assets/css/magnific-popup.css", rel: "stylesheet" },
        { href: "/assets/css/styles.css", rel: "stylesheet" },
      ]
      styles.forEach(item => { 
          const link = document.createElement("link")
          link.href = item.href
          link.rel = "stylesheet"
          link.async = false
          document.head.appendChild(link)
      }) */
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
          </Switch>
        </div>
    </div>
  )
}
