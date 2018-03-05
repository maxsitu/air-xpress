import React from 'react';

import { css, withStyles } from 'react-with-styles';
import createClass from 'create-react-class';
import PropTypes from 'prop-types';
import Select from 'react-select';

const FLAVOURS = [
  { label: 'Chocolate', value: 'chocolate' },
  { label: 'Vanilla', value: 'vanilla' },
  { label: 'Strawberry', value: 'strawberry' },
  { label: 'Caramel', value: 'caramel' },
  { label: 'Cookies and Cream', value: 'cookiescream' },
  { label: 'Peppermint', value: 'peppermint' },
  { label: 'Coconut', value: 'coconut' },
  { label: 'Raspberry', value: 'raspberry' },
];

const WHY_WOULD_YOU = [
  { label: 'Chocolate (are you crazy?)', value: 'chocolate', disabled: true },
].concat(FLAVOURS.slice(1));

let MultiSelectField = createClass({
  componentWillMount() {
    this.setState({
      removeSelected: true,
      disabled: false,
      crazy: false,
      stayOpen: false,
      value: [],
      rtl: false,
    });
  },

  handleSelectChange (value) {
    console.log('You\'ve selected:', value);
    this.setState({ value });
  },

  toggleCheckbox (e) {
    this.setState({
      [e.target.name]: e.target.checked,
    });
  },

  toggleRtl (e) {
    let rtl = e.target.checked;
    this.setState({ rtl });
  },

  render () {
    console.dir(this.state);
    const { crazy, disabled, stayOpen, value } = this.state;
    const { styles } = this.props;
    const options = crazy ? WHY_WOULD_YOU : FLAVOURS;
    return (
      <div className="section">
        <h3 className="section-heading">{this.props.label} </h3>
        <Select
          closeOnSelect={!stayOpen}
          disabled={disabled}
          multi
          onChange={this.handleSelectChange}
          options={options}
          placeholder="Select your favourite(s)"
          removeSelected={this.state.removeSelected}
          rtl={this.state.rtl}
          simpleValue
          pageSize={2}
          value={value}
        />

        <div className="checkbox-list">
          <label className="checkbox">
            <input {...css(
              styles.MultiSelectField__checkbox
            )} type="checkbox"  name="removeSelected" checked={this.state.removeSelected} onChange={this.toggleCheckbox} />
            <span>Remove selected options</span>
          </label>
          <label className="checkbox">
            <input  {...css(
              styles.MultiSelectField__checkbox
            )} type="checkbox" name="disabled" checked={this.state.disabled} onChange={this.toggleCheckbox} />
            <span className="checkbox-label">Disable the control</span>
          </label>
          <label className="checkbox">
            <input {...css(
              styles.MultiSelectField__checkbox
            )} type="checkbox" name="crazy" checked={crazy} onChange={this.toggleCheckbox} />
            <span className="checkbox-label">I don't like Chocolate (disabled the option)</span>
          </label>
          <label className="checkbox">
            <input  {...css(
              styles.MultiSelectField__checkbox
            )}type="checkbox" name="stayOpen" checked={stayOpen} onChange={this.toggleCheckbox}/>
            <span className="checkbox-label">Stay open when an Option is selected</span>
          </label>
          <label className="checkbox">
            <input  {...css(
              styles.MultiSelectField__checkbox
            )} type="checkbox" name="rtl" checked={this.state.rtl} onChange={this.toggleCheckbox} />
            <span className="checkbox-label">rtl</span>
          </label>
        </div>
      </div>
    );
  }
});

MultiSelectField.displayName = 'MultiSelectField';
MultiSelectField.propTypes = {
  label: PropTypes.string,
};

export default withStyles(({multiSelect: {checkbox}}) => ({
  MultiSelectField__checkbox: {
    position: 'relative',
    marginLeft: checkbox.marginLeft,
    marginRight: checkbox.marginRight,
  }
}))(MultiSelectField)