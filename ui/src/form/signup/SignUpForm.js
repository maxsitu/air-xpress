import React from 'react';
import {SubmissionError, Field, reduxForm} from 'redux-form';

import {SessionService} from '../../common';
import validate from './do/validate';
import {logIn} from '../../Redux/Action';


const renderField = ({input, label, type, meta: {asyncValidating, touched, error}}) => (
  <div>
    <label>{label}</label>
    <div className={asyncValidating ? 'async-validating' : ''}>
      <input {...input} type={type} placeholder={label}/>
      {touched && error && <span>{error}</span>}
    </div>
  </div>
);

function registerUser(dispatch) {
  return function (regInfo) {
    const sessionService = SessionService.getInstance();

    return sessionService.register(regInfo)
      .then((resp) => {
        // console.log('SignUpForm:', resp);
        dispatch(logIn());
      })
      .catch((err) => {
        throw new SubmissionError(Object.assign(err, {
          _error: 'Login failed!'
        }));
      });
  }

}

class SignUpForm extends React.Component {

  render() {
    const {handleSubmit, pristine, reset, submitting, error, dispatch} = this.props;
    return (
      <form onSubmit={handleSubmit(registerUser(dispatch))}>
        <Field name="login" type="text" component={renderField} label="Username"/>
        <Field name="email" type="email" component={renderField} label="Email"/>
        <Field name="password" type="password" component={renderField} label="Password"/>
        <Field name="confirmPassword" type="password" component={renderField} label="Confirm Password"/>
        {error && <strong>{error}</strong>}
        <div>
          <button type="submit" disabled={submitting}>Login</button>
          <button type="button" disabled={pristine || submitting} onClick={reset}>Clear Values</button>
        </div>
      </form>
    )
  }
}
;

export default reduxForm({
  form: 'signUpForm', // a unique identifier for this form
  validate
})(SignUpForm)