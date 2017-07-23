import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Route,
    Switch,
} from 'react-router-dom';

import LoginContainer from './Login';
import SoSRootContainer from './sos';
import AuditContainer from './sos/Audit';
import AuditRiskLimitContainer from './sos/AuditRiskLimit';
import AuditRoundContainer from './sos/AuditRound';
import AuditSeedContainer from './sos/AuditSeed';
import ContestDetailContainer from './sos/ContestDetail';
import ContestOverviewContainer from './sos/ContestOverview';
import CountyDetailContainer from './sos/CountyDetail';
import CountyOverviewContainer from './sos/CountyOverview';


export interface RootContainerProps {
    store: Store<any>;
}

type RouteDef = [string, React.ComponentClass];

const makeRoute = (def: RouteDef) => {
    const [path, Component] = def;
    return <Route exact key={ path } path={ path } component={ Component } />;
};

const routes: RouteDef[] = [
    ['/login', LoginContainer],
    ['/sos', SoSRootContainer],
    ['/sos/audit', AuditContainer],
    ['/sos/audit/risk-limit', AuditRiskLimitContainer],
    ['/sos/audit/round', AuditRoundContainer],
    ['/sos/audit/seed', AuditSeedContainer],
    ['/sos/contest', ContestOverviewContainer],
    ['/sos/contest/:contestId', ContestDetailContainer],
    ['/sos/county', CountyOverviewContainer],
    ['/sos/county/:countyId', CountyDetailContainer],
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
