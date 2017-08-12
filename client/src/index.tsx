import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { AppContainer } from 'react-hot-loader';
import { createStore } from 'redux';

import { RootContainer } from './component/RootContainer';
import rootReducer from './reducer/root';


const store = createStore(rootReducer);
const rootEl = document.getElementById('root');


const render = (NextRootContainer: any) => {
    const appContainer = (
        <AppContainer>
            <NextRootContainer store={ store } />
        </AppContainer>
    );

    ReactDOM.render(appContainer, rootEl);
};

render(RootContainer);

if (module.hot) {
    module.hot.accept('./component/RootContainer', () => {
        const NextRootContainer =
            (require('./component/RootContainer') as any).RootContainer;

        render(NextRootContainer);
    });
}
