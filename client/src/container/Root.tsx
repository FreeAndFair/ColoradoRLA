import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Route,
    Switch,
} from 'react-router-dom';

import AuditContainer from './Audit';
import BallotContainer from './Ballot';
import HomeContainer from './Home';
import LoginContainer from './Login';
import ReportContainer from './Report';
import RoundContainer from './Round';
import SeedContainer from './Seed';
import UploadContainer from './Upload';


export interface RootContainerProps {
    store: Store<any>;
}

type RouteDef = [string, React.ComponentClass];

const makeRoute = (def: RouteDef) => {
    const [path, Component] = def;
    return <Route exact key={ path } path={ path } component={ Component } />;
};

const routes: RouteDef[] = [
    ['/', HomeContainer],
    ['/login', LoginContainer],
    ['/audit', AuditContainer],
    ['/audit/ballot', BallotContainer],
    ['/audit/report', ReportContainer],
    ['/audit/round', RoundContainer],
    ['/audit/seed', SeedContainer],
    ['/audit/upload', UploadContainer],
];

export class RootContainer extends React.Component<RootContainerProps, void> {
    public render() {
        const { store } = this.props;

        return (
            <Provider store={ store }>
                <Router>
                    <Switch>
                        { routes.map(makeRoute) }
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
