import React from 'react'
import {connect} from 'react-redux';
import {ButtonToolbar, Button} from 'react-bootstrap'
class TicketOrderForm extends React.Component {
  render() {
    return (
      <div><h1>Order a Ticket</h1>
        <ButtonToolbar>
          <Button > Default </Button>
          <Button bsStyle="primary"> Primary </Button>
          <Button
            bsStyle="success"> Success </Button>
          < Button
            bsStyle="info"> Info </Button>
          < Button
            bsStyle="warning"> Warning </Button>
          < Button
            bsStyle="danger"> Danger </Button>
          < Button
            bsStyle="link"> Link </Button>
        </ButtonToolbar>
      </div>
    );
  }
}
export default connect(
  state => {
    return {
      isLoggedIn: state.isLoggedIn
    };
  }
)(TicketOrderForm);