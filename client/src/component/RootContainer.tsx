import * as React from 'react';
import { connect, Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Redirect,
    Route,
    Switch,
} from 'react-router-dom';

import RootRedirectContainer from './RootRedirectContainer';

import AuditBoardPageContainer from './County/AuditBoard/PageContainer';

import CountyAuditPageContainer from './County/Audit/PageContainer';
import CountyDashboardPageContainer from './County/Dashboard/PageContainer';

import GlossaryContainer from './Help/GlossaryContainer';
import HelpRootContainer from './Help/HelpRootContainer';
import ManualContainer from './Help/ManualContainer';

import NextLoginContainer from './Login/Container';

import DOSDefineAuditPageContainer from './DOS/DefineAudit/PageContainer';
import DOSDefineAuditReviewPageContainer from './DOS/DefineAudit/ReviewPageContainer';
import DOSDefineAuditSeedPageContainer from './DOS/DefineAudit/SeedPageContainer';
import DOSDefineSelectContestsPageContainer from './DOS/DefineAudit/SelectContestsPageContainer';

import DOSContestDetailPageContainer from './DOS/Contest/DetailPageContainer';
import DOSContestOverviewPageContainer from './DOS/Contest/OverviewPageContainer';
import DOSCountyDetailPageContainer from './DOS/County/DetailPageContainer';
import DOSCountyOverviewPageContainer from './DOS/County/OverviewPageContainer';

import DOSDashboardContainer from './DOS/Dashboard/PageContainer';


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
    ['/county/board', AuditBoardPageContainer],
    ['/county/audit', CountyAuditPageContainer],
    ['/help', HelpRootContainer],
    ['/help/glossary', GlossaryContainer],
    ['/help/manual', ManualContainer],
    ['/sos', DOSDashboardContainer],
    ['/sos/audit', DOSDefineAuditPageContainer],
    ['/sos/audit/seed', DOSDefineAuditSeedPageContainer],
    ['/sos/audit/select-contests', DOSDefineSelectContestsPageContainer],
    ['/sos/audit/review', DOSDefineAuditReviewPageContainer],
    ['/sos/contest', DOSContestOverviewPageContainer],
    ['/sos/contest/:contestId', DOSContestDetailPageContainer],
    ['/sos/county', DOSCountyOverviewPageContainer],
    ['/sos/county/:countyId', DOSCountyDetailPageContainer],
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
