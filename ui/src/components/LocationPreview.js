import React from 'react';
import {connect} from 'react-redux';
import {withGoogleMap, GoogleMap, Marker} from "react-google-maps";
import {LOCATION_PREVIEW_CLICKED} from "../constants/actionTypes";

const LocationMap = withGoogleMap((props) =>

  <GoogleMap
    defaultZoom={props.zoomSize}
    defaultCenter={{lat: props.location.geoLat, lng: props.location.geoLon}}
    center={{lat: props.location.geoLat, lng: props.location.geoLon}}
    defaultOptions={
      {fullscreenControl: false}
    }
  >
    {props.isMarkerShown && <Marker position={{lat: props.location.geoLat, lng: props.location.geoLon}}/>}
  </GoogleMap>
);

const mapDispatchToProps = dispatch => ({
  onLocationPreviewClicked: payload => dispatch({type: LOCATION_PREVIEW_CLICKED, payload})
});

class LocationPreview  extends React.Component {
  constructor() {
    super();
    this.clickHandler = ev => {
      ev.preventDefault();
      this.props.onLocationPreviewClicked(this.props.location.id);
    }
  }
  render(){
    const props = this.props;
    return (
      <div className="ax-location-preview" onClick={this.clickHandler}>
        <div className="ax-location-preview-fields">
          <span>{props.location.name} ({props.location.code})</span>
        </div>
        <div className="ax-location-preview-map">
          <LocationMap
            isMarkerShown={props.isMarkerShown}
            loadingElement={props.loadingElement}
            containerElement={props.containerElement}
            mapElement={props.mapElement}
            location={props.location}
            zoomSize={props.zoomSize}
          />
        </div>
      </div>
    );
  }
};


export default connect(null, mapDispatchToProps)(LocationPreview);