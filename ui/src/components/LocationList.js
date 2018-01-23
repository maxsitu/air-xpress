import React from 'react';
import {connect} from 'react-redux';
import fetch from '../fetch';
import {LOCATION_PAGE_LOADED, LOCATION_PAGE_UNLOADED} from "../constants/actionTypes";
import LocationPreview from './LocationPreview';

const mapStateToProps = state => ({
  ...state.locationList
});

const mapDispatchToProps = dispatch => ({
  onLoad: payload => dispatch({type: LOCATION_PAGE_LOADED, payload}),
  onUnLoad: () => dispatch({type: LOCATION_PAGE_UNLOADED})
});

class LocationList extends React.Component {
  componentWillMount () {
    this.props.onLoad(fetch.Location.all())
  }

  componentWillUnmount () {
    this.props.onUnLoad();
  }

  render () {
    return (
      <div className="location-list">
        {
          this.props.locations && this.props.locations.map(location => {
              return (
                <div key={location.id} className="location">

                  <LocationPreview
                    isMarkerShown
                    loadingElement={<div style={{ height: `100%` }} />}
                    containerElement={<div style={{ height: `100px`, width: `100px` }} />}
                    mapElement={<div style={{ height: `100%` }} />}
                    location={location}
                    zoomSize={12}
                  />
                </div>
              )
            }
          )
        }
      </div>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(LocationList);