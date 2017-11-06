import PropTypes from 'prop-types';
import React from 'react';
import {connect} from 'react-redux';
import {Navbar, Nav, NavItem, Button} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';

import {UserStatusAction} from '../state/action'
import SessionService from '../common/session/session.service';

class NavBar extends React.Component {
  static propTypes = {
    isLoggedIn: PropTypes.bool.isRequired,
    logout: PropTypes.func.isRequired
  };

  render() {
    const {isLoggedIn, logout} = this.props;

    return (
      <Navbar>
        <Nav>
          {
            !isLoggedIn && (
              <LinkContainer to="/login">
                <NavItem>Login</NavItem>
              </LinkContainer>
            )
          }
          {
            !isLoggedIn && (
              <LinkContainer to="/signup">
                <NavItem>Sign Up</NavItem>
              </LinkContainer>
            )
          }
          {
            isLoggedIn && (
              <LinkContainer to="/order">
                <NavItem>Order Ticket</NavItem>
              </LinkContainer>
            )
          }

          {
            isLoggedIn && (
              <Button onClick={logout}>Logout</Button>
            )
          }
        </Nav>
      </Navbar>);
  }
}

function mapStateToProps(state, ownProps) {
  return {
    isLoggedIn: state.userStatus.isLoggedIn
  }
}

function mapDispatchToProps(dispatch) {
  return {
    logout: () => {
      SessionService.getInstance().logout()
        .then(() => {
          dispatch(UserStatusAction.setUserStatusIsLoggedIn(false));
        }).catch(e => {
        console.log("Exception logging out in Navbar");
        console.error(e)
      });
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(NavBar);