import React from 'react'
import {connect} from 'react-redux'
import {Field, reduxForm} from 'redux-form'

import {SessionService} from '../../common';
import validate from './do/validate';
import {UserStatusAction} from '../../state/action';


class LoginValidationForm extends React.Component {
  constructor(props) {
    super(props);
    this.redirectPath = props.redirectPath;
    this.sessionService = SessionService.getInstance();
  }

  render() {
    const {handleSubmit, pristine, reset, submitting, login} = this.props;
    return (
      <form onSubmit={handleSubmit(login)}>
        <Field name="login" type="text" component={this.renderField} label="Username"/>
        <Field name="password" type="password" component={this.renderField} label="Password"/>
        <Field name="rememberMe" type="checkbox" component={this.renderCheckbox} label="Remember me"
               defaultChecked={false}/>
        <div>
          <button type="submit" disabled={submitting}>Login</button>
          <button type="button" disabled={pristine || submitting} onClick={reset}>Clear values</button>
        </div>
      </form>
    )
  }

  renderField({input, label, type, meta: {asyncValidating, touched, error}}) {
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

  renderCheckbox({input, label, defaultChecked, type}) {
    delete input.checked;
    return (
      <div>
        <label>{label}</label>
        <div>
          <input {...input} type={type} defaultChecked={defaultChecked}/>
        </div>
      </div>
    )
  }
}

let loginValidationForm = reduxForm({
  form: 'loginForm', // a unique identifier for this form
  validate
})(LoginValidationForm);

function mapDispatchToProps(dispatch) {
  return {
    login: (loginInput) => {
      loginInput.rememberMe = !!loginInput.rememberMe;
      const sessionService = SessionService.getInstance();
      return sessionService.login(loginInput).then(userInfo =>
        dispatch(UserStatusAction.setUserStatusIsLoggedIn(true))
      );
    }
  }
}

export default connect(
  null, mapDispatchToProps
)(loginValidationForm);