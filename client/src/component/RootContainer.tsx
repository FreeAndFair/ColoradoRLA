import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Redirect,
    Route,
    Switch,
} from 'react-router-dom';

import CountyContestDetailContainer from './county/ContestDetailContainer';
import CountyContestOverviewContainer from './county/ContestOverviewContainer';

import CountyAuditContainer from './county/audit/CountyAuditContainer';
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

const LoginRoute = ({ loggedIn, page: Page, ...rest }: any) => {
    const render = (props: any) => {
        if (loggedIn) {
            return <Page { ...props } />;
        }

        return <Redirect to='/login' />;
    };

    return <Route render={ render } { ...rest } />;
};

type RouteDef = [string, React.ComponentClass];

const makeRoute = (loggedIn: boolean) => (def: RouteDef) => {
    const [path, Page] = def;
    return (
        <LoginRoute
            exact
            key={ path }
            path={ path }
            page={ Page }
            loggedIn={ loggedIn }
        />
    );
};

const routes: RouteDef[] = [
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
        const { loggedIn } = store.getState();

        return (
            <Provider store={ store }>
                <Router>
                    <Switch>
                        <Route exact path='/login' component={ LoginContainer } />
                        { routes.map(makeRoute(loggedIn)) }
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
