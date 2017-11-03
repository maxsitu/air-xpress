import $ from 'jquery';
import _ from 'lodash';
import React, {Component} from 'react';
import {Provider} from 'react-redux'
import logo from './logo.svg';
import './App.css';

import {createStore, combineReducers} from 'redux'
import {reducer as reduxFormReducer} from 'redux-form'
import LoginValidationForm from './form/login/LoginValidationForm';
import SignUpForm from './form/signup/SignUpForm';
import AppRouter from './Router/Router'

import UserServiceFactory from './component/account/user.service.factory';


const reducer = combineReducers({
    form: reduxFormReducer // mounted under "form"
});
const store = createStore(reducer);


const userService = UserServiceFactory.create();
const loginUser = values => {
  const loginInput = _.assignIn({}, values, {rememberMe: true});
  userService.login(loginInput)
    .done((loggedUser) =>
      window.alert(`User login successfully: \n\n${JSON.stringify(values, null, 2)}`)
    ).fail((req, status, err) =>
      window.alert(`Failed. Error: ${err}`));
};
const showResults = values =>
    new Promise(resolve => {
        setTimeout(() => {  // simulate server latency
            window.alert(`You submitted:\n\n${JSON.stringify(values, null, 2)}`)
            resolve()
        }, 500)
    });

function _create(values) {
    return $.ajax({
        url: '/api/users',
        type: 'POST',
        data: {
            username: this.state.username,
            password: this.state.password,
            email: this.state.email
        },
        beforeSend: function () {
            this.setState({loading: true});
        }.bind(this)
    });
}

const something = () =>
  <Provider store={store}>
    <div>
      <LoginValidationForm onSubmit={loginUser}/>
    </div>
  </Provider>
;

class App extends Component {


    // render() {
    //     return (
    //         <div className="App">
    //             <div className="App-header">
    //                 <img src={logo} className="App-logo" alt="logo"/>
    //                 <h2>Welcome to React</h2>
    //             </div>
    //             <p className="App-intro">
    //                 To get started, edit <code>src/App.js</code> and save to reload.
    //             </p>
    //
    //
    //         </div>
    //     );
    // }
  render() {
    return (<Provider store={store}>
      <AppRouter/>
    </Provider>)
  }
}

export default App;
