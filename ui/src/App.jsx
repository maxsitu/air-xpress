import React from 'react';
import {render} from 'react-dom';
import {Provider} from 'react-redux';
import store from './Redux/Store/Store'
import route from './Router/Router'


render(
  <Provider store={store}>
    {route}
  </Provider>,
  document.getElementById('root')
);
