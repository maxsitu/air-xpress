import React from 'react';
import {connect} from 'react-redux';
import FlightStep from './FlightStep';
import moment from 'moment';
import fetch from '../fetch';
// import { DateRangeInput } from "@blueprintjs/datetime";

import 'react-select/dist/react-select.css';
import {
  FLIGHT_PLAN_PAGE_UNLOADED,
  FLIGHT_PLAN_PAGE_LOADED,
  UPDATE_FIELD_FLIGHT_PLAN,
  FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP
} from "../constants/actionTypes";



function mapStateToProps (state) {
  return ({
    ...state.flightPlanEditor
  });
}

const mapDispatchToProps = dispatch => ({
  onLoad: payload =>
    dispatch({type: FLIGHT_PLAN_PAGE_LOADED}),
  onUnLoad: payload =>
    dispatch({type: FLIGHT_PLAN_PAGE_UNLOADED}),
  onUpdateField: (idx, key, value) =>
    dispatch({type: UPDATE_FIELD_FLIGHT_PLAN, idx, key, value}),
  onAddFlightStep: payload =>
    dispatch({type: FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP}),
  onRemoveFlightStep: payload =>
    dispatch({type: FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP})
});

class FlightPlan extends React.Component {
  constructor () {
    super();

    this.minLimit = 2;
  }

  componentWillMount () {
    this.props.onLoad();
  }

  componentWillUnmount () {
    this.props.onUnLoad();
  }


  render () {
    if (! this.props.flightSteps) {
      return null;
    }

    return (
      <div className="container">
        <form>
          <fieldset>
            {
              this.props.flightSteps.map((step, idx) => (
                <fieldset key={idx}>
                  <FlightStep
                    minLimit={this.minLimit}
                    idx={idx}
                    fieldPrefix="flight-plan"
                    onUpdateField={this.props.onUpdateField}
                    onAddFlightStep={this.props.onAddFlightStep}
                    onRemoveFlightStep={this.props.onRemoveFlightStep}
                    flightStep={this.props.flightSteps[idx]}
                    isLastOfList={idx === (this.props.flightSteps.length - 1)}
                  />
                </fieldset>)
              )
            }
          </fieldset>
        </form>
      </div>
    );
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(FlightPlan);