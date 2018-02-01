import React from 'react';
import {connect} from 'react-redux';
import {Async} from 'react-select';
import { DateTimePicker } from "@blueprintjs/datetime";
import 'react-select/dist/react-select.css';
import {
  FLIGHT_PLAN_PAGE_UNLOADED,
  FLIGHT_PLAN_PAGE_LOADED,
  UPDATE_FIELD_FLIGHT_PLAN,
  FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP
} from "../constants/actionTypes";

const getOptions = (input) => {
  return Promise.resolve({
    options: [
      {value: 'hello', label: 'HALLO'},
      {value: 'world', label: 'WORLD'},
      {value: 'foo', label: 'FOO'}],
    complete: true
  });
};

class FlightStep extends React.Component {
  constructor () {
    super();

    this.onAddFlightStep = evt => {
      evt.preventDefault();
      this.props.onAddFlightStep();
    };
    this.onRemoveFlightStep = evt => {
      evt.preventDefault();
      this.props.onRemoveFlightStep();
    }
  }

  handleChange (idx, fieldName) {
    const onUpdateField = this.props.onUpdateField;
    return function (selectedOption) {
      onUpdateField(idx, fieldName, selectedOption.value);
      console.log(`Selected: ${selectedOption.value}`);
    }

  };

  componentWillReceiveProps (nextProps) {
    const idx = nextProps.idx;

  }

  render () {
    const {flightSteps, fieldPrefix, idx, minLimit} = this.props;

    if (!flightSteps) {
      return null;
    }

    const flightStep = flightSteps[idx] || {},
      isLastOfList = (idx === (flightSteps.length - 1)),
      isShowRemoveDestination = isLastOfList && (idx >= minLimit),
      isShowAddDestination = isLastOfList && (idx >= minLimit-1)
    ;

    const now = new Date();


    return (
      <fieldset>
        <fieldset>
          <span>From: </span>
          <Async
            value={flightStep.from_loc}
            name={`${fieldPrefix}_from_loc_${idx}`}
            loadOptions={getOptions}
            onChange={this.handleChange(idx, 'from_loc')}
          />
        </fieldset>

        <fieldset>
          <span>To: </span>
          <Async
            value={flightStep.to_loc}
            name={`${fieldPrefix}_to_loc_${idx}`}
            loadOptions={getOptions}
            onChange={this.handleChange(idx, 'to_loc')}
          />
        </fieldset>

        <fieldset>
          <DateTimePicker format={"YYYY-MM-DD HH:mm:ss"} minDate={now}/>
        </fieldset>

        <p>
          {
            isShowRemoveDestination && (
              <a href="" onClick={this.onRemoveFlightStep}>Remove destination </a>
            )
          }
          <span hidden={!isShowRemoveDestination}>|</span>
          {
            isShowAddDestination && (
              <a href="" onClick={this.onAddFlightStep}>Add destination</a>
            )
          }
        </p>

      </fieldset>
    );
  }
};

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
                    flightSteps={this.props.flightSteps}
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