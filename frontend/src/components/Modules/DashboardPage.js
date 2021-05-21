import React, { useEffect } from 'react';
import Dashboard from './../Dashboard/Dashboard';

import './DashboardPage.css'

export default function DashboardPage() {

  useEffect(() => {
      /* let scripts = [
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
      })  */

      /* let styles = [
        { href: "https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback", rel: "stylesheet" },
        { href: "https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,600;0,700;1,400&display=swap", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/fontawesome-free/css/all.min.css", rel: "stylesheet" },
        { href: "/https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/icheck-bootstrap/icheck-bootstrap.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/jqvmap/jqvmap.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/dist/css/adminlte.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/overlayScrollbars/css/OverlayScrollbars.min.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/daterangepicker/daterangepicker.css", rel: "stylesheet" },
        { href: "/assets/DASHBOARD/plugins/summernote/summernote-bs4.min.css", rel: "stylesheet" },
      ]
      styles.forEach(item => { 
          const link = document.createElement("link")
          link.href = item.href
          link.rel = "stylesheet"
          link.async = false
          document.head.appendChild(link)
      }) */

     
  }, [])

  return (
    <div>
        <Dashboard />
    </div>
  )
}
