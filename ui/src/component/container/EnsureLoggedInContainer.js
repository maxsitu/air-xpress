import React from 'react';
import {connect} from 'react-redux';
import {Route} from 'react-router-dom';
import PropTypes from 'prop-types';

import {setRedirectPath, navigateTo} from '../../Redux/Action';
import TicketOrderForm from '../../form/ticketOrder/TicketOrderForm';

const ticketOrderForm = ({location}) => {
  return <TicketOrderForm location={location}/>;
};

function _handleNonLoggedIn(props) {
  const {dispatch, currentPath, isLoggedIn, loginPath} = props;

  if (!isLoggedIn && currentPath !== loginPath ) {
    dispatch(setRedirectPath(currentPath));
    dispatch(navigateTo(loginPath))
  }
}

class EnsureLoggedInContainer extends React.Component {
  static propTypes = {
    currentPath:  PropTypes.string.isRequired,
    loginPath:    PropTypes.string.isRequired,
    isLoggedIn:   PropTypes.bool.isRequired
  };

  componentWillMount() {
    _handleNonLoggedIn(this.props);
  }

  render() {
    const {isLoggedIn} = this.props;
    return isLoggedIn ? (
        <Route path="/order" component={ticketOrderForm}/>
    ): null;
  }
}

function mapStateToProps(state, ownProps) {
  return {
    isLoggedIn: state.isLoggedIn,
    currentPath: ownProps.location.pathname,
    loginPath:  ownProps.loginPath
  }
}

export default connect(mapStateToProps)(EnsureLoggedInContainer);
