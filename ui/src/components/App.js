// import agent from '../agent';
import Header from './Header';
import fetch from '../fetch';
import React from 'react';
import { connect } from 'react-redux';
import { APP_LOAD, REDIRECT } from '../constants/actionTypes';
import Home from '../components/Home';
import Login from '../components/Login';
import Register from '../components/Register';
import Settings from '../components/Settings';
import LocationList from "./LocationList";
import LocationEditor from "./LocationEditor";
import PlaneEditor from "./PlaneEditor";
import FlightPlan from "./FlightPlan";
import PilotPreferenceEditor from "./PilotPreferenceEditor";
import { store } from '../store';
import { Route, Switch } from 'react-router-dom';
import { push } from 'react-router-redux';
import Cookies from 'universal-cookie';

const mapStateToProps = state => {
  return {
    appLoaded: state.common.appLoaded,
    appName: state.common.appName,
    currentUser: state.common.currentUser,
    redirectTo: state.common.redirectTo
  }};

const mapDispatchToProps = dispatch => ({
  onLoad: (payload, token) =>
    dispatch({ type: APP_LOAD, payload, token, skipTracking: true }),
  onRedirect: () =>
    dispatch({ type: REDIRECT })
});

const _cookies = new Cookies();

class App extends React.Component {
  componentWillReceiveProps(nextProps) {
    if (nextProps.redirectTo) {
      // this.context.router.replace(nextProps.redirectTo);
      store.dispatch(push(nextProps.redirectTo));
      this.props.onRedirect();
    }
  }

  componentWillMount() {
    const token = _cookies.get('_jt_ui_sessiondata');
    if (token) {
      fetch.setToken(token);
    }

    this.props.onLoad(token ? fetch.Auth.current() : null, token);
  }

  render() {
    if (this.props.appLoaded) {
      return (
        <div>
          <Header
            appName={this.props.appName}
            currentUser={this.props.currentUser} />
          <Switch>
            <Route exact path="/" component={Home}/>
            <Route path="/login" component={Login} />
            <Route path="/register" component={Register} />
            {/*<Route path="/editor/:slug" component={Editor} />*/}
            {/*<Route path="/editor" component={Editor} />*/}
            {/*<Route path="/article/:id" component={Article} />*/}
            <Route path="/settings" component={Settings} />
            <Route path="/locations" component={LocationList}/>
            <Route path="/locationEditor/:locationId" component={LocationEditor}/>
            <Route path="/locationEditor" component={LocationEditor}/>
            <Route path="/planeEditor" component={PlaneEditor}/>
            <Route path="/flightPlan" component={FlightPlan}/>
            <Route path="/pilotPreference" component={PilotPreferenceEditor}/>

            {/*<Route path="/@:username/favorites" component={ProfileFavorites} />*/}
            {/*<Route path="/@:username" component={Profile} />*/}
          </Switch>
        </div>
      );
    }
    return (
      <div>
        <Header
          appName={this.props.appName}
          currentUser={this.props.currentUser} />
      </div>
    );
  }
}

// App.contextTypes = {
//   router: PropTypes.object.isRequired
// };

export default connect(mapStateToProps, mapDispatchToProps)(App);
