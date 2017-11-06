import React from 'react'
import {connect} from 'react-redux'
import {Field, reduxForm} from 'redux-form'

import {SessionService} from '../../common';
import validate from './do/validate';



class LoginValidationForm extends React.Component {
  constructor(props) {
    super(props);
    this.redirectPath   = props.redirectPath;
    this.sessionService = SessionService.getInstance();
  }

  render() {
    const {handleSubmit, pristine, reset, submitting, submitLogin} = this.props;
    return (
      <form onSubmit={handleSubmit(submitLogin)}>
        <Field name="login" type="text" component={renderField} label="Username"/>
        <Field name="password" type="password" component={renderField} label="Password"/>
        <Field name="rememberMe" type="checkbox" component={renderField} label="Remember me"/>
        <div>
          <button type="submit" disabled={submitting}>Sign up</button>
          <button type="button" disabled={pristine || submitting} onClick={reset}>Clear Values</button>
        </div>
      </form>
    )
  }
}

let loginValidationForm = reduxForm({
  form: 'loginForm', // a unique identifier for this form
  validate
})(LoginValidationForm);

loginValidationForm = connect(
  null, {
    submitLogin: submitLogin
  }
)(loginValidationForm);

function  renderField ({input, label, type, meta: {asyncValidating, touched, error}}) {
    return (
        <div>
            <label>{label}</label>
            <div className={asyncValidating ? 'async-validating' : ''}>
                <input {...input} type={type} placeholder={label}/>
                {touched && error && <span>{error}</span>}
            </div>
        </div>
    );
}

function submitLogin(loginInput) {
    const sessionService = SessionService.getInstance();

    return (dispatch) => {
        return sessionService.login(loginInput);
    }
}

export default loginValidationForm;