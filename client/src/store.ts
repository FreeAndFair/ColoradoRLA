import { applyMiddleware, createStore } from 'redux';
import createSagaMiddleware from 'redux-saga';
import thunk from 'redux-thunk';

import session from './session';

import defaultState from './reducer/defaultState';
import rootReducer from './reducer/root';


export const sagaMiddleware = createSagaMiddleware();

// Compute default app state, given any existing user session.
//
// In our usage, this should happen exactly once: when the scripts are
// first evaluated. We do this so that (for example) refreshing the
// browser doesn't appear to log the user out.
export function preloadedState() {
    const s = session.get();

    if (!s) {
        return defaultState.login();
    }

    switch (s.type) {
    case 'county': return defaultState.county();
    case 'dos': return defaultState.dos();
    default: return defaultState.login();
    }
}

export const store = createStore(
    rootReducer,
    preloadedState(),
    applyMiddleware(thunk, sagaMiddleware),
);


export default store;
