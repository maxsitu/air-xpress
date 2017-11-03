import React from 'react';
import {Link} from 'react-router-dom';
import {connect} from 'react-redux';

import SessionService from '../../common/session/session.service';
import {logOut} from '../../Redux/Action';
import {Navbar, Nav, NavItem} from 'react-bootstrap';

function submitLogout() {

  return (dispatch) => {
    SessionService.getInstance().logout().catch(() => {
      console.log("here");
    }).then(() => {
      dispatch(logOut());
    });
  }
}

class NavBar extends React.Component {
  render() {
    const {isLoggedIn, submitLogout} = this.props;

    return (
      <Navbar>
        <Nav>
          {
            !isLoggedIn && (
              <NavItem>
                <Link to="/login">Login</Link>
              </NavItem>
            )
          }
          {
            !isLoggedIn && (
              <NavItem>
                <Link to="/signup">Sign Up</Link>
              </NavItem>
            )
          }
          {
            isLoggedIn && (
              <NavItem>
                <Link to="/order">Order Ticket</Link>
              </NavItem>
            )
          }

          {
            isLoggedIn ? (
              <NavItem onClick={submitLogout}>Logout</NavItem>
            ) : null
          }
        </Nav>
      </Navbar>);
  }
}

function mapStateToProps(state, ownProps) {
  return {
    isLoggedIn: state.isLoggedIn
  }
}

export default connect(mapStateToProps, {
  submitLogout: submitLogout
})(NavBar);