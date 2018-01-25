import ListErrors from './ListErrors';
import React from 'react';
import fetch from '../fetch';
import { connect } from 'react-redux';
import {
  LOCATION_EDITOR_PAGE_LOADED,
  LOCATION_SUBMITTED,
  LOCATION_EDITOR_PAGE_UNLOADED,
  UPDATE_FIELD_LOCATION_EDITOR
} from '../constants/actionTypes';
import {withGoogleMap, GoogleMap, Marker} from "react-google-maps";

const LocationMap = withGoogleMap((props) => {
  const geoLat = parseFloat(props.geoLat);
  const geoLon = parseFloat(props.geoLon);
  return  <GoogleMap
    defaultZoom={props.zoomSize}
    center={{lat: geoLat, lng: geoLon}}
    defaultOptions={
      {fullscreenControl: false}
    }
  >
    {props.isMarkerShown && <Marker position={{lat: geoLat, lng: geoLon}}/>}
  </GoogleMap>
});

const mapStateToProps = state => ({
  ...state.locationEditor
});

const mapDispatchToProps = dispatch => ({
  onLoad: payload =>
    dispatch({ type: LOCATION_EDITOR_PAGE_LOADED, payload }),
  onSubmit: payload =>
    dispatch({ type: LOCATION_SUBMITTED, payload }),
  onUnload: payload =>
    dispatch({ type: LOCATION_EDITOR_PAGE_UNLOADED }),
  onUpdateField: (key, value) =>
    dispatch({ type: UPDATE_FIELD_LOCATION_EDITOR, key, value })
});

class Editor extends React.Component {
  constructor() {
    super();

    const updateFieldEvent =
      key => ev => this.props.onUpdateField(key, ev.target.value);
    this.changeCode = updateFieldEvent('code');
    this.changeName = updateFieldEvent('name');
    this.changeGeoLat = updateFieldEvent('geoLat');
    this.changeGeoLon = updateFieldEvent('geoLon');

    this.submitForm = ev => {
      ev.preventDefault();
      const {locationId, code, name, geoLat, geoLon} = this.props;

      const promise = this.props.locationId ?
        fetch.Location.update(locationId, code, name, geoLat, geoLon) :
        fetch.Location.create(code, name, geoLat, geoLon);

      this.props.onSubmit(promise);
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.match.params.locationId !== nextProps.match.params.locationId) {
      if (nextProps.match.params.locationId) {
        this.props.onUnload();
        return this.props.onLoad(fetch.Location.findById(this.props.match.params.locationId));
      }
      this.props.onLoad(null);
    }
  }

  componentWillMount() {
    if (this.props.match.params.locationId) {
      return this.props.onLoad(fetch.Location.findById(this.props.match.params.locationId));
    }
    this.props.onLoad(null);
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

              <LocationMap
                zoomSize={12}
                geoLat={this.props.geoLat || 0}
                geoLon={this.props.geoLon || 0}
                isMarkerShown={true}
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `200px`, width: `100%` }} />}
                mapElement={<div style={{ height: `100%` }} />}
              >
              </LocationMap>
              <form>
                <fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control form-control-lg"
                      type="text"
                      placeholder="Airport Code"
                      value={this.props.code || ''}
                      onChange={this.changeCode} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control"
                      type="text"
                      placeholder="Airport Name"
                      value={this.props.name || ''}
                      onChange={this.changeName} />
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control"
                      type="number"
                      placeholder="Geo Latitude"
                      value={this.props.geoLat || 0}
                      onChange={this.changeGeoLat}>
                    </input>
                  </fieldset>

                  <fieldset className="form-group">
                    <input
                      className="form-control"
                      type="number"
                      placeholder="Geo Longitude"
                      value={this.props.geoLon || 0}
                      onChange={this.changeGeoLon}>
                    </input>
                  </fieldset>

                  <button
                    className="btn btn-lg pull-xs-right btn-primary"
                    type="button"
                    disabled={this.props.inProgress}
                    onClick={this.submitForm}>
                    { this.props.locationId ? 'Update' : 'Create'} Location
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

export default connect(mapStateToProps, mapDispatchToProps)(Editor);
