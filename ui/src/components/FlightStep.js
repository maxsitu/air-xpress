import React from "react";
import PropTypes from 'prop-types';
import ReactDOM from "react-dom";
import fetch from "../fetch";
import {Async} from 'react-select';
import DateRangePickerWrapper from './daterange/DateRangePickerWrapper';

const FIELD_FROM_LOCATION = 'from_loc';
const FIELD_TO_LOCATION = 'to_loc';
const FIELD_TIME_RANGE = 'time_range';

const propTypes = {
  onUpdateField: PropTypes.func.isRequired,
  onAddFlightStep: PropTypes.func.isRequired,
  onRemoveFlightStep: PropTypes.func.isRequired,
  flightStep: PropTypes.object.isRequired,
  fieldPrefix: PropTypes.string.isRequired,
  idx: PropTypes.number.isRequired,
  minLimit: PropTypes.number.isRequired,
  isLastOfList: PropTypes.bool.isRequired,
  error: PropTypes.object
};

class FlightStep extends React.Component {
  constructor (props) {
    super(props);

    this.onAddFlightStep = evt => {
      evt.preventDefault();
      this.props.onAddFlightStep();
    };

    this.onRemoveFlightStep = evt => {
      evt.preventDefault();
      this.props.onRemoveFlightStep();
    };

  }

  handleChange (idx, fieldName) {
    let __this = this;
    const { onUpdateField } = this.props;
    return function (selectedOption) {
      switch (fieldName) {
        case FIELD_FROM_LOCATION:
          __this.refs[`${FIELD_TO_LOCATION}_${idx}`] && __this.refs[`${FIELD_TO_LOCATION}_${idx}`].loadOptions();
          onUpdateField(idx, fieldName, selectedOption.value);
          break;
        case FIELD_TO_LOCATION:
          __this.refs[`${FIELD_FROM_LOCATION}_${idx}`] && __this.refs[`${FIELD_FROM_LOCATION}_${idx}`].loadOptions();
          onUpdateField(idx, fieldName, selectedOption.value);
          break;
        case FIELD_TIME_RANGE:
          onUpdateField(idx, fieldName, [selectedOption.startDate, selectedOption.endDate]);
          break;
        default:
          break;
      }

      console.log(`Selected: ${selectedOption}`);
    }

  };

  getLocationOptions (field) {
    const flightStep = this.props.flightStep;
    return (input) =>
      fetch.Location.findByNamePrefix(input)
        .then(function (locations) {
          return {
            options: locations.map(function (loc) {
              return {
                value: loc.code,
                label: `${loc.name} (${loc.code})`,
                disabled: (loc.code === flightStep[field])
              }
            }),
            complete: true
          }
        });
  };

  render () {
    const {flightStep, fieldPrefix, idx, minLimit, isLastOfList, error} = this.props;

    const isShowRemoveDestination = isLastOfList && (idx >= minLimit),
      isShowAddDestination = isLastOfList;

    return (
      <fieldset className={`FlightStep_${idx}`}>
        <fieldset>
          <span>From: </span>
          <span hidden={!(error && error[FIELD_FROM_LOCATION])} className={'ax-flight-step__error-msg'}>{error ? error[FIELD_FROM_LOCATION] : ''}</span>
          <Async
            ref={`${FIELD_FROM_LOCATION}_${idx}`}
            autoload={false}
            cache={false}
            placeholder="Departing airport"
            value={flightStep[FIELD_FROM_LOCATION]}
            name={`${fieldPrefix}_${FIELD_FROM_LOCATION}_${idx}`}
            loadOptions={this.getLocationOptions(FIELD_TO_LOCATION)}
            onChange={this.handleChange(idx, FIELD_FROM_LOCATION)}
          />
        </fieldset>

        <fieldset>
          <span>To: </span>
          <span hidden={!(error && error[FIELD_TO_LOCATION])} className={'ax-flight-step__error-msg'}>{error ? error[FIELD_TO_LOCATION] : ''}</span>
          <Async
            ref={`${FIELD_TO_LOCATION}_${idx}`}
            autoload={false}
            cache={false}
            placeholder="Arriving airport"
            value={flightStep[FIELD_TO_LOCATION]}
            name={`${this.fieldPrefix}_${FIELD_TO_LOCATION}_${idx}`}
            loadOptions={this.getLocationOptions(FIELD_FROM_LOCATION)}
            onChange={this.handleChange(idx, FIELD_TO_LOCATION)}
          />
        </fieldset>

        <fieldset>
          <span hidden={!(error && error[FIELD_TIME_RANGE])} className={'ax-flight-step__error-msg'}>{error ? error[FIELD_TIME_RANGE] : ''}</span>
          <DateRangePickerWrapper
            ref={item => {this.dateRangePickerWrapper = item}}
            onDatesChange={this.handleChange(idx, FIELD_TIME_RANGE)}/>
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

  focusStartDate() {
    ReactDOM.findDOMNode(this.dateRangePickerWrapper.dateRangePicker).querySelector(`.DateRangePicker_1 input#startDate`).focus();
  }

  focusEndDate() {
    ReactDOM.findDOMNode(this.dateRangePickerWrapper.dateRangePicker).querySelector(`.DateRangePicker_1 input#endDate`).focus();
  }
}

FlightStep.propTypes = propTypes;

export {FIELD_FROM_LOCATION, FIELD_TO_LOCATION, FIELD_TIME_RANGE};
export default FlightStep;