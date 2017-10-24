import * as React from 'react';
import { connect, Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Redirect,
    Route,
    Switch,
} from 'react-router-dom';

import LoginRoute from './LoginRoute';
import RootRedirectContainer from './RootRedirectContainer';

import AuditBoardPageContainer from './County/AuditBoard/PageContainer';

import CountyAuditPageContainer from './County/Audit/PageContainer';
import CountyDashboardPageContainer from './County/Dashboard/PageContainer';

import NextLoginContainer from './Login/Container';

import DOSDefineAuditReviewPageContainer from './DOS/DefineAudit/ReviewPageContainer';
import DOSDefineAuditSeedPageContainer from './DOS/DefineAudit/SeedPageContainer';
import DOSDefineAuditSelectContestsPageContainer from './DOS/DefineAudit/SelectContestsPageContainer';
import DOSDefineAuditStartPageContainer from './DOS/DefineAudit/StartPageContainer';

import DOSContestDetailPageContainer from './DOS/Contest/DetailPageContainer';
import DOSContestOverviewPageContainer from './DOS/Contest/OverviewPageContainer';
import DOSCountyDetailPageContainer from './DOS/County/DetailPageContainer';
import DOSCountyOverviewPageContainer from './DOS/County/OverviewPageContainer';

import DOSDashboardContainer from './DOS/Dashboard/PageContainer';


export interface RootContainerProps {
    store: Store<AppState>;
}

export class RootContainer extends React.Component<RootContainerProps> {
    public render() {
        const { store } = this.props;

        return (
            <Provider store={ store }>
                <Router>
                    <Switch>
                        <Route exact
                               path='/login'
                               component={ NextLoginContainer } />
                        <LoginRoute exact
                                    path='/'
                                    page={ RootRedirectContainer } />
                        <LoginRoute exact
                                    path='/county'
                                    page={ CountyDashboardPageContainer } />
                        <LoginRoute exact
                                    path='/county/board'
                                    page={ AuditBoardPageContainer } />
                        <LoginRoute exact
                                    path='/county/audit'
                                    page={ CountyAuditPageContainer } />
                        <LoginRoute exact
                                    path='/sos'
                                    page={ DOSDashboardContainer } />
                        <LoginRoute exact
                                    path='/sos/audit'
                                    page={ DOSDefineAuditStartPageContainer } />
                        <LoginRoute exact
                                    path='/sos/audit/seed'
                                    page={ DOSDefineAuditSeedPageContainer } />
                        <LoginRoute exact
                                    path='/sos/audit/select-contests'
                                    page={ DOSDefineAuditSelectContestsPageContainer } />
                        <LoginRoute exact
                                    path='/sos/audit/review'
                                    page={ DOSDefineAuditReviewPageContainer } />
                        <LoginRoute exact
                                    path='/sos/contest'
                                    page={ DOSContestOverviewPageContainer } />
                        <LoginRoute exact
                                    path='/sos/contest/:contestId'
                                    page={ DOSContestDetailPageContainer } />
                        <LoginRoute exact
                                    path='/sos/county'
                                    page={ DOSCountyOverviewPageContainer } />
                        <LoginRoute exact
                                    path='/sos/county/:countyId'
                                    page={ DOSCountyDetailPageContainer } />
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
