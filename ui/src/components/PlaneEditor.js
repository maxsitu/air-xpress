import React from 'react';
import fetch from '../fetch';
import {connect} from 'react-redux';
import ListErrors from './ListErrors';
import {
  PLANE_EDITOR_PAGE_LOADED,
  PLANE_EDITOR_PAGE_UNLOADED,
  PLANE_SUBMITTED,
  UPDATE_FIELD_PLANE_EDITOR
} from "../constants/actionTypes";

const mapStateToProps = state => ({
  ...state.planeEditor
});

const mapDispatchToProps = dispatch => ({
  onLoad: payload =>
    dispatch({type: PLANE_EDITOR_PAGE_LOADED}),
  onSubmit: payload =>
    dispatch({type: PLANE_SUBMITTED, payload}),
  onUnload: payload =>
    dispatch({type: PLANE_EDITOR_PAGE_UNLOADED}),
  onUpdateField: (key, value) =>
    dispatch({type: UPDATE_FIELD_PLANE_EDITOR, key, value})
});

class PlaneEditor extends React.Component {
  constructor () {
    super();

    const updateFieldEvent =
      key => ev => this.props.onUpdateField(key, ev.target.value);
    this.changeNNo = updateFieldEvent('nNo');
    this.changeManufacturerName = updateFieldEvent('manufacturerName');
    this.changeSerialNo = updateFieldEvent('serialNo');
    this.changeModel = updateFieldEvent('model');
    this.changePilotSeats = updateFieldEvent('pilotSeats');
    this.changeMinPilot = updateFieldEvent('minPilot');
    this.changeCustomerSeats = updateFieldEvent('customerSeats');

    this.submitForm = ev => {
      ev.preventDefault();

      const {nNo, manufacturerName, serialNo, model, pilotSeats, minPilot, customerSeats} = this.props;
      this.props.onSubmit(
        fetch.Plane.create(nNo, manufacturerName, serialNo, model, pilotSeats, minPilot, customerSeats)
      );
    }
  }

  componentWillMount() {
    this.props.onLoad();
  }

  componentWillUnmount() {
    this.props.onUnload();
  }

  render() {
    return (
      <div className="editor-page">
        <div className="container page">
          <div className="row">
            <div className="col-md-10 offset-md-1 col-xs-12">

              <ListErrors errors={this.props.errors}></ListErrors>
              <form>
                <fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="N Number"
                      value={this.props.nNo || ''}
                      onChange={this.changeNNo} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Manufacturer Name"
                      value={this.props.manufacturerName || ''}
                      onChange={this.changeManufacturerName} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Serial Number"
                      value={this.props.serialNo || ''}
                      onChange={this.changeSerialNo} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Model"
                      value={this.props.model || ''}
                      onChange={this.changeModel} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Pilot Seats"
                      value={this.props.pilotSeats || ''}
                      onChange={this.changePilotSeats} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Minimum Pilot Required"
                      value={this.props.minPilot || ''}
                      onChange={this.changeMinPilot} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Customer Seats"
                      value={this.props.customerSeats || ''}
                      onChange={this.changeCustomerSeats} />
                  </fieldset>

                  <button
                    className="btn btn-lg pull-xs-right btn-primary"
                    type="button"
                    disabled={this.props.inProgress}
                    onClick={this.submitForm}>
                    { this.props.nNo ? 'Update' : 'Create'} Plane
                  </button>
                </fieldset>
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(PlaneEditor);