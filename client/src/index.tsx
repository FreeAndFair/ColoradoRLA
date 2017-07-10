import * as React from 'react';
import * as ReactDOM from 'react-dom';

import { AppContainer } from 'react-hot-loader';

import { App } from './container/App';


const rootEl = document.getElementById('root');

const render = (C: any) => {
    const appContainer = (
      <AppContainer>
        <C />
      </AppContainer>
    );

    ReactDOM.render(appContainer, rootEl);
}

render(App);

if (module.hot) {
    module.hot.accept('./container/App', () => {
        const NextApp = (require('./container/App') as any).App;

        render(NextApp);
    });
}
