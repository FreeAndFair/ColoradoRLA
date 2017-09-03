import * as React from 'react';
import { connect, Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Redirect,
    Route,
    Switch,
} from 'react-router-dom';

import RootRedirectContainer from './RootRedirectContainer';

import CountyContestDetailContainer from './county/ContestDetailContainer';
import CountyContestOverviewContainer from './county/ContestOverviewContainer';

import AuditBoardPageContainer from './county/AuditBoard/PageContainer';

import CountyAuditPageContainer from './county/Audit/PageContainer';
import CountyDashboardPageContainer from './county/CountyDashboard/PageContainer';

import GlossaryContainer from './help/GlossaryContainer';
import HelpRootContainer from './help/HelpRootContainer';
import ManualContainer from './help/ManualContainer';

import NextLoginContainer from './login/next/LoginContainer';

import AuditContainer from './sos/audit/AuditContainer';
import AuditReviewContainer from './sos/audit/AuditReviewContainer';
import AuditSeedContainer from './sos/audit/AuditSeedContainer';
import SelectContestsPageContainer from './sos/audit/SelectContestsPageContainer';

import ContestDetailContainer from './sos/contest/ContestDetailContainer';
import ContestOverviewContainer from './sos/contest/ContestOverviewContainer';
import CountyDetailContainer from './sos/county/CountyDetailContainer';
import CountyOverviewContainer from './sos/county/CountyOverviewContainer';

import DOSDashboardContainer from './sos/DOSDashboard/Container';


export interface RootContainerProps {
    store: Store<any>;
}

const UnconnectedLoginRoute = ({ loggedIn, page: Page, ...rest }: any) => {
    const render = (props: any) => {
        if (loggedIn) {
            return <Page { ...props } />;
        }

        const from  = props.location.pathname || '/';
        const to = {
            pathname: '/login',
            state: { from },
        };
        return <Redirect to={ to } />;
    };

    return <Route render={ render } { ...rest } />;
};

const LoginRoute: any = connect(
    ({ loggedIn }: any) => ({ loggedIn }),
)(UnconnectedLoginRoute);

type RouteDef = [string, React.ComponentClass];

const makeRoute = (store: any) => (def: RouteDef) => {
    const [path, Page] = def;

    return (
        <LoginRoute
            exact
            key={ path }
            path={ path }
            page={ Page }
        />
    );
};

const routes: RouteDef[] = [
    ['/', RootRedirectContainer],
    ['/county', CountyDashboardPageContainer],
    ['/county/sign-in', AuditBoardPageContainer],
    ['/county/audit', CountyAuditPageContainer],
    ['/county/contest', CountyContestOverviewContainer],
    ['/county/contest/:contestId', CountyContestDetailContainer],
    ['/help', HelpRootContainer],
    ['/help/glossary', GlossaryContainer],
    ['/help/manual', ManualContainer],
    ['/sos', DOSDashboardContainer],
    ['/sos/audit', AuditContainer],
    ['/sos/audit/seed', AuditSeedContainer],
    ['/sos/audit/select-contests', SelectContestsPageContainer],
    ['/sos/audit/review', AuditReviewContainer],
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
                        <Route exact path='/login' component={ NextLoginContainer } />
                        { routes.map(makeRoute(store)) }
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
