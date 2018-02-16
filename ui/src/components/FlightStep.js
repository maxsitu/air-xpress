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
    const {flightStep, fieldPrefix, idx, minLimit, isLastOfList} = this.props;

    const isShowRemoveDestination = isLastOfList && (idx >= minLimit),
      isShowAddDestination = isLastOfList;

    return (
      <fieldset>
        <fieldset>
          <span>From: </span>
          <Async
            ref={`${FIELD_FROM_LOCATION}_${idx}`}
            cache={false}
            value={flightStep[FIELD_FROM_LOCATION]}
            name={`${fieldPrefix}_${FIELD_FROM_LOCATION}_${idx}`}
            loadOptions={this.getLocationOptions(FIELD_TO_LOCATION)}
            onChange={this.handleChange(idx, FIELD_FROM_LOCATION)}
          />
        </fieldset>

        <fieldset>
          <span>To: </span>
          <Async
            ref={`${FIELD_TO_LOCATION}_${idx}`}
            cache={false}
            value={flightStep[FIELD_TO_LOCATION]}
            name={`${this.fieldPrefix}_${FIELD_TO_LOCATION}_${idx}`}
            loadOptions={this.getLocationOptions(FIELD_FROM_LOCATION)}
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
}

export default FlightStep;