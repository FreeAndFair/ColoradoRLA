import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Route,
    Switch,
} from 'react-router-dom';

import CountyContestDetailContainer from './county/ContestDetailContainer';
import CountyContestOverviewContainer from './county/ContestOverviewContainer';
import CountyAuditContainer from './county/CountyAuditContainer';
import CountyHomeContainer from './county/home/CountyHomeContainer';

import GlossaryContainer from './help/GlossaryContainer';
import HelpRootContainer from './help/HelpRootContainer';
import ManualContainer from './help/ManualContainer';

import LoginContainer from './login/LoginContainer';

import AuditContainer from './sos/AuditContainer';
import AuditRiskLimitContainer from './sos/AuditRiskLimitContainer';
import AuditRoundContainer from './sos/AuditRoundContainer';
import AuditSeedContainer from './sos/AuditSeedContainer';
import ContestDetailContainer from './sos/ContestDetailContainer';
import ContestOverviewContainer from './sos/ContestOverviewContainer';
import CountyDetailContainer from './sos/CountyDetailContainer';
import CountyOverviewContainer from './sos/CountyOverviewContainer';
import SoSRootContainer from './sos/SoSRootContainer';


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
    ['/county', CountyHomeContainer],
    ['/county/audit', CountyAuditContainer],
    ['/county/contest', CountyContestOverviewContainer],
    ['/county/contest/:contestId', CountyContestDetailContainer],
    ['/help', HelpRootContainer],
    ['/help/glossary', GlossaryContainer],
    ['/help/manual', ManualContainer],
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
