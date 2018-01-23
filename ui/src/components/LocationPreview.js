import React from 'react';
import {withGoogleMap, GoogleMap, Marker} from "react-google-maps";

const LocationMap = withGoogleMap((props) =>

  <GoogleMap
    defaultZoom={props.zoomSize}
    defaultCenter={{lat: props.location.geoLat, lng: props.location.geoLon}}
  >
    {props.isMarkerShown && <Marker position={{lat: props.location.geoLat, lng: props.location.geoLon}}/>}
  </GoogleMap>
);

const LocationPreview = props => {
  return (
    <div className="location-preview">
      <div className="location-preview-fields">
        <span>{props.location.name} ({props.location.code})</span>
      </div>
      <div className="location-preview-map">
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
};


export default LocationPreview;