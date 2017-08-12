import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { AppContainer } from 'react-hot-loader';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';

import { RootContainer } from './component/RootContainer';
import rootReducer from './reducer/root';


const store = createStore(rootReducer, applyMiddleware(thunk));
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
