import PropTypes from 'prop-types';
import React from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {Navbar, Nav, NavItem, Button} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';

import {UserStatusAction} from '../state/action';
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
        <Navbar.Header>
          <Navbar.Brand>
            <Link to="/">Air-Xpress</Link>
          </Navbar.Brand>
        </Navbar.Header>
        <Nav>
          {!isLoggedIn && (
            <NavItem>
              <LinkContainer to="/login">
                <div>Login</div>
              </LinkContainer>
            </NavItem>
          )}
          {!isLoggedIn && (
            <NavItem>
              <LinkContainer to="/signup">
                <div>Sign Up</div>
              </LinkContainer>
            </NavItem>
          )}
          {isLoggedIn && (
            <NavItem>
              <LinkContainer to="/order">
                <div>Order Ticket</div>
              </LinkContainer>
            </NavItem>
          )}
          {isLoggedIn && (
            <NavItem>
              <Button onClick={logout}>Logout</Button>
            </NavItem>
          )}
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