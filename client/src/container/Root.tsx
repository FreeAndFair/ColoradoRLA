import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Route,
    Switch,
} from 'react-router-dom';

import App from './App';


export interface RootProps {
    store: Store<any>;
}

type RouteDef = [string, React.ComponentClass];

const makeRoute = (def: RouteDef) => {
    const [path, Component] = def;
    return <Route exact key={ path } path={ path } component={ Component } />;
};

const routes: RouteDef[] = [
    ['/', App],
];

export class Root extends React.Component<RootProps, void> {
    public render() {
        const { store } = this.props;

        return (
            <Provider store={store}>
                <Router>
                    <Switch>
                        { routes.map(makeRoute) }
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
