import * as React from 'react';
import { Provider, Store } from 'react-redux'
import {
    BrowserRouter as Router,
    Route,
} from 'react-router-dom';

import App from './App';


export interface RootProps {
    store: Store<any>;
}

export class Root extends React.Component<RootProps, void> {
    render() {
        const { store } = this.props;

        return (
            <Provider store={store}>
                <Router>
                    <Route path='/' component={ App } />
                </Router>
            </Provider>
        );
    }
}
