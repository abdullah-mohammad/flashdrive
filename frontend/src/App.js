import React from 'react';
import StandardPage from './components/Modules/StandardPage';
import DashboardPage from './components/Modules/DashboardPage';
import { Switch, Route } from "react-router-dom";

export default function App() {
  return (
    <Switch>
      <Route path="/dashboard" component={DashboardPage} />
      <Route path="/" component={StandardPage} />
    </Switch>
  )
}
