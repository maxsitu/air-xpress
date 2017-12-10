import React from 'react';

import Navbar from '../component/Navbar';

class IndexPage extends React.Component {
  render() {
    return (
      <div>
        <Navbar/>
        <div className="jt-index-page">
          <h1>This is Index page</h1>
          <textarea defaultValue="Should write something here."/>
        </div>
      </div>
    )
  }
}

export default IndexPage