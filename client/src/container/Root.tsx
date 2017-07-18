import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Route,
    Switch,
} from 'react-router-dom';

import Audit from './Audit';
import Ballot from './Ballot';
import Home from './Home';
import Login from './Login';
import Report from './Report';
import Round from './Round';
import Seed from './Seed';
import Upload from './Upload';


export interface RootProps {
    store: Store<any>;
}

type RouteDef = [string, React.ComponentClass];

const makeRoute = (def: RouteDef) => {
    const [path, Component] = def;
    return <Route exact key={ path } path={ path } component={ Component } />;
};

const routes: RouteDef[] = [
    ['/', Home],
    ['/login', Login],
    ['/audit', Audit],
    ['/audit/ballot', Ballot],
    ['/audit/report', Report],
    ['/audit/round', Round],
    ['/audit/seed', Seed],
    ['/audit/upload', Upload],
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
