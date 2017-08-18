import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { AppContainer } from 'react-hot-loader';

import { applyMiddleware, createStore } from 'redux';
import createSagaMiddleware from 'redux-saga';
import thunk from 'redux-thunk';

import { RootContainer } from './component/RootContainer';

import rootReducer from './reducer/root';

import rootSaga from './saga/root';


const sagaMiddleware = createSagaMiddleware();
export const store = createStore(
    rootReducer,
    applyMiddleware(thunk, sagaMiddleware),
);
sagaMiddleware.run(rootSaga);

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
