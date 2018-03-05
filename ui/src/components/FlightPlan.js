import React from 'react';
import {connect} from 'react-redux';
import FlightStep from './FlightStep';
import 'react-select/dist/react-select.css';
import {FIELD_FROM_LOCATION, FIELD_TO_LOCATION, FIELD_TIME_RANGE} from './FlightStep';
import {
  FLIGHT_PLAN_PAGE_UNLOADED,
  FLIGHT_PLAN_PAGE_LOADED,
  UPDATE_FIELD_FLIGHT_PLAN,
  FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_VALIDATE_ERROR
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
    dispatch({type: FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP}),
  onValidateError: errors =>
    dispatch({type: FLIGHT_PLAN_PAGE_VALIDATE_ERROR, errors})
});

class FlightPlan extends React.Component {
  constructor () {
    super();

    this.minLimit = 2;
    this.isValid = true;
  }

  componentWillMount () {
    this.props.onLoad();
  }

  componentWillUnmount () {
    this.props.onUnLoad();
  }

  validate () {
    const {flightSteps} = this.props;
    let isValid = true;
    this.errors = flightSteps.map(step => {
      const err = {};
      if (!step[FIELD_FROM_LOCATION]) {
        err[FIELD_FROM_LOCATION] = 'Please select from location';
        this.isValid = false;
      }

      if (!step[FIELD_TO_LOCATION]) {
        err[FIELD_TO_LOCATION] = 'Please select to location';
        this.isValid = false;
      }

      if (!step[FIELD_TIME_RANGE] ||
        !step[FIELD_TIME_RANGE][0] ||
        !step[FIELD_TIME_RANGE][1]) {
        err[FIELD_TIME_RANGE] = 'Please select date range';
        this.isValid = false;
      }

      return err;
    });

    return isValid
  }

  submitForm = ev => {
    ev.preventDefault();
    this.validate();
    this.props.onValidateError(this.errors);
  };

  render () {
    const {flightSteps, errors, onUpdateField, onAddFlightStep, onRemoveFlightStep} = this.props;

    if (!flightSteps) {
      return null;
    }

    return (
      <div className="container">
        <form>
          <fieldset>
            {
              flightSteps.map((step, idx) => (
                <fieldset key={idx}>
                  <FlightStep
                    minLimit={this.minLimit}
                    idx={idx}
                    ref={item => {
                      this[`flightStepRef_${idx}`] = item
                    }}
                    fieldPrefix="flight-plan"
                    onUpdateField={onUpdateField}
                    onAddFlightStep={onAddFlightStep}
                    onRemoveFlightStep={onRemoveFlightStep}
                    flightStep={flightSteps[idx]}
                    isLastOfList={idx === (flightSteps.length - 1)}
                    error={errors && idx < errors.length && errors[idx] && errors[idx]}
                  />
                </fieldset>)
              )
            }
            <div>
              <button
                className="btn btn-lg pull-xs-right btn-primary"
                type="button"
                onClick={this.submitForm.bind(this)}
              >
                Search
              </button>
            </div>
          </fieldset>
        </form>
      </div>
    );
  }

  componentDidUpdate(prevProps, prevState) {
    const {flightSteps, lastUpdatedIndex} = this.props;
    if (lastUpdatedIndex != null) {
      let i = lastUpdatedIndex;

      let startDate = this.startDateAt(i),
        endDate = this.endDateAt(i);

      if (startDate===null || endDate === null) return;

      if (i !== 0) {
        let prevEndDate = this.endDateAt(i-1);
        if (prevEndDate!=null && startDate.isBefore(prevEndDate)) {
          this.focusFlightStepEndDate(i-1);
          return;
        }
      }

      if (i !== flightSteps.length-1) {
        let nextStartDate = this.startDateAt(i+1);
        if (nextStartDate!=null && endDate.isAfter(nextStartDate)) {
          this.focusFlightStepStartDate(i+1);
          return;
        }
      }
    }
  }

  startDateAt(idx) {
    const currTimeRange = this.timeRangeAt(idx);
    return currTimeRange && currTimeRange[0];
  }

  endDateAt(idx) {
    const currTimeRange = this.timeRangeAt(idx);
    return currTimeRange && currTimeRange[1];
  }

  timeRangeAt(idx) {
    const {flightSteps} = this.props;
    return flightSteps && flightSteps[idx][FIELD_TIME_RANGE];
  }

  focusFlightStepStartDate (idx) {
    this[`flightStepRef_${idx}`].focusStartDate();
  }

  focusFlightStepEndDate (idx) {
    this[`flightStepRef_${idx}`].focusEndDate();
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(FlightPlan);