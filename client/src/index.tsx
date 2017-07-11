import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { AppContainer } from 'react-hot-loader';
import { createStore } from 'redux';

import { Root } from './container/Root';
import rootReducer from './reducer/root';


const store = createStore(rootReducer);
const rootEl = document.getElementById('root');

const render = (NextRoot: any) => {
    const appContainer = (
        <AppContainer>
            <NextRoot store={store} />
        </AppContainer>
    );

    ReactDOM.render(appContainer, rootEl);
};

render(Root);

if (module.hot) {
    module.hot.accept('./container/Root', () => {
        const NextRoot = (require('./container/Root') as any).Root;

        render(NextRoot);
    });
}
