const validate = values => {
    const errors = {};
    if (!values.login) {
        errors.login = 'Username is required';
    }
    if (!values.email) {
        errors.email = 'Email si required'
    } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)) {
      errors.email = 'Invalid email address'
    }
    if (!values.password) {
        errors.password = 'Password is required';
    } else if (!values.confirmPassword || values.confirmPassword !== values.password) {
        errors.confirmPassword = `Passwords don't match`;
    }

    return errors;
};

export default validate