import ThemedStyleSheet from 'react-with-styles/lib/ThemedStyleSheet';
import aphroditeInterface from 'react-with-styles-interface-aphrodite';
import ReactDatesDefaultTheme from 'react-dates/lib/theme/DefaultTheme';
import DefaultTheme from '../theme/DefaultTheme';

ThemedStyleSheet.registerInterface(aphroditeInterface);
ThemedStyleSheet.registerTheme({
  reactDates: {
    ...ReactDatesDefaultTheme.reactDates,
  },
  ...DefaultTheme,
});