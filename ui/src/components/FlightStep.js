import React from "react";
import fetch from "../fetch";
import moment from "moment/moment";
import {Async} from 'react-select';
import DatetimeRangePicker from 'react-datetime-range-picker';

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

    let __this = this;
    const onUpdateField = this.props.onUpdateField;
    return function (selectedOption) {
      switch (fieldName) {
        case 'from_loc':
          __this.refs[`to_loc_${idx}`] && __this.refs[`to_loc_${idx}`].loadOptions();
          break;
        case 'to_loc':
          __this.refs[`from_loc_${idx}`] && __this.refs[`from_loc_${idx}`].loadOptions();
          break;
        default:
          break;
      }

      if (fieldName === 'time_range') {
        onUpdateField(idx, fieldName, [selectedOption.start, selectedOption.end]);
      } else {
        onUpdateField(idx, fieldName, selectedOption.value);
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
            onChange={this.handleChange(idx, 'from_loc')}
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
            onChange={this.handleChange(idx, 'to_loc')}
          />
        </fieldset>

        <fieldset>
          {/*<DateRangeInput format={"YYYY-MM-DD HH:mm:ss"} minDate={now}/>*/}
          <DatetimeRangePicker
            startDate={this.flightStep.time_range && this.flightStep.time_range[0] || moment().toDate()}
            endDate={this.flightStep.time_range && this.flightStep.time_range[1] || moment().toDate()}
            dateFormat={"YYYY-MM-DD HH:mm:ss"}
            isValidStartDate={ this.isValidStartDate.bind(this) }
            isValidEndDate={ this.isValidEndDate.bind(this) }
            onChange={this.handleChange(idx, 'time_range')}
          />
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