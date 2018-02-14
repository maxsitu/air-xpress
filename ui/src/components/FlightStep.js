import React from "react";
import fetch from "../fetch";
import moment from "moment/moment";
import {Async} from 'react-select';
import DateRangePickerWrapper from './daterange/DateRangePickerWrapper';

const FIELD_FROM_LOCATION = 'from_loc';
const FIELD_TO_LOCATION = 'to_loc';
const FIELD_TIME_RANGE = 'time_range';

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

    let focusedInput = null;
    this.state = {
      focusedInput,
      startDate: null,
      endDate: null
    };
  }

  handleChange (idx, fieldName) {
    let __this = this;
    const onUpdateField = this.props.onUpdateField;
    return function (selectedOption) {
      switch (fieldName) {
        case FIELD_FROM_LOCATION:
          __this.refs[`to_loc_${idx}`] && __this.refs[`to_loc_${idx}`].loadOptions();
          onUpdateField(idx, fieldName, selectedOption.value);
          break;
        case FIELD_TO_LOCATION:
          __this.refs[`from_loc_${idx}`] && __this.refs[`from_loc_${idx}`].loadOptions();
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

  componentWillReceiveProps (nextProps) {
    const idx = nextProps.idx;

  }

  getFromOptions (input) {
    let flightStep = this.flightStep;
    return fetch.Location.findByNamePrefix(input)
      .then(function (locations) {
        return {
          options: locations.map(function (loc) {
            return {
              value: loc.code,
              label: `${loc.name} (${loc.code})`,
              disabled: (loc.code === flightStep.to_loc)
            }
          }),
          complete: true
        }
      });
  };

  getToOptions (input) {
    let flightStep = this.flightStep;
    return fetch.Location.findByNamePrefix(input)
      .then(function (locations) {
        return {
          options: locations.map(function (loc) {
            return {
              value: loc.code,
              label: `${loc.name} (${loc.code})`,
              disabled: (loc.code === flightStep.from_loc)
            }
          }),
          complete: true
        }
      });
  };

  isValidStartDate(current) {
    let now = moment();
    return current.isAfter( now ) && (this.flightStep.time_range && current.isBefore(moment(this.flightStep.time_range[1])) || true);
  }

  isValidEndDate(current) {
    let lastEndDate = moment().add(2, 'month');
    return current.isBefore( lastEndDate ) && (this.flightStep.time_range && current.isAfter(moment(this.flightStep.time_range[0])) || true);
  }

  render () {
    const {flightSteps, fieldPrefix, idx, minLimit} = this.props;

    if (!flightSteps) {
      return null;
    }

    this.flightStep = flightSteps[idx] || {};
    const isLastOfList = (idx === (flightSteps.length - 1)),
      isShowRemoveDestination = isLastOfList && (idx >= minLimit),
      isShowAddDestination = isLastOfList && (idx >= minLimit-1)
    ;

    return (
      <fieldset>
        <fieldset>
          <span>From: </span>
          <Async
            ref={`from_loc_${idx}`}
            cache={false}
            value={this.flightStep.from_loc}
            name={`${fieldPrefix}_from_loc_${idx}`}
            loadOptions={this.getFromOptions.bind(this)}
            onChange={this.handleChange(idx, FIELD_FROM_LOCATION)}
          />
        </fieldset>

        <fieldset>
          <span>To: </span>
          <Async
            ref={`to_loc_${idx}`}
            cache={false}
            value={this.flightStep.to_loc}
            name={`${this.fieldPrefix}_to_loc_${idx}`}
            loadOptions={this.getToOptions.bind(this)}
            onChange={this.handleChange(idx, FIELD_TO_LOCATION)}
          />
        </fieldset>

        <fieldset>
          <DateRangePickerWrapper onDatesChange={this.handleChange(idx, FIELD_TIME_RANGE)}/>
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

export default FlightStep;