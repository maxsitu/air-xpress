import NavBar from '../component/navbar/NavBar';
import createBrowserHistory from 'history/createBrowserHistory';
import AppContainer from '../component/container/AppContainer';

import React from 'react'
import {
  BrowserRouter,
  Route
} from 'react-router-dom'

const customHistory = createBrowserHistory();

const AppRoute = (
  <BrowserRouter history={customHistory} >
    <div>

      <NavBar/>

      <hr/>

      <Route path="/" component={AppContainer}/>
    </div>
  </BrowserRouter>);


export default AppRoute;