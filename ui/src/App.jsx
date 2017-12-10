import React from 'react';
import ReactDOM from 'react-dom';
import {createStore, combineReducers, applyMiddleware} from 'redux';
import {Provider} from 'react-redux';
import {Route, Switch, Redirect} from 'react-router-dom';
import {ConnectedRouter, routerReducer, routerMiddleware } from 'react-router-redux';
import createHistory from 'history/createBrowserHistory';
import thunk from 'redux-thunk';
import {UserStatusAction} from './state/action';
import reducers from './state/reducer'
import Navbar from './component/Navbar'
import IndexPage from './page/IndexPage';
import LoginPage from './page/LoginPage';
import SignUpPage from './page/SingUpPage';
import SessionService from "./common/session/session.service";

const history = createHistory();
const middleware = routerMiddleware(history);

const store = createStore(combineReducers(
  {
    ...reducers,
    router: routerReducer
  }),
  applyMiddleware(thunk, middleware)
);

const sessionService = SessionService.getInstance();
sessionService.refreshSession().then(() =>
  sessionService.isLoggedIn() && store.dispatch(UserStatusAction.setUserStatusIsLoggedIn(true))
);


ReactDOM.render(
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <div>
        <Switch>
          <Route exact path="/" render={() => <IndexPage/>}/>
          <Route exact path="/login" render={() => <LoginPage currentPath="/login"/>}/>
          <Route exact path="/signUp" render={() => <SignUpPage currentPath="/signUp"/>}/>
          <Redirect to="/"/>
        </Switch>

      </div>
    </ConnectedRouter>
  </Provider>,
  document.getElementById('root')
);
