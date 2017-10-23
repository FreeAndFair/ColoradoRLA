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

import GlossaryContainer from './Help/GlossaryContainer';
import HelpRootContainer from './Help/HelpRootContainer';
import ManualContainer from './Help/ManualContainer';

import NextLoginContainer from './Login/Container';

import DOSDefineAuditReviewPageContainer from './DOS/DefineAudit/ReviewPageContainer';
import DOSDefineAuditSeedPageContainer from './DOS/DefineAudit/SeedPageContainer';
import DOSDefineAduitSelectContestsPageContainer from './DOS/DefineAudit/SelectContestsPageContainer';
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
                        <Route exact path='/login' component={ NextLoginContainer } />
                        <LoginRoute path='/'
                                    page={ RootRedirectContainer } />
                        <LoginRoute path='/county'
                                    page={ CountyDashboardPageContainer } />
                        <LoginRoute path='/county/board'
                                    page={ AuditBoardPageContainer } />
                        <LoginRoute path='/county/audit'
                                    page={ CountyAuditPageContainer } />
                        <LoginRoute path='/sos'
                                    page={ DOSDashboardContainer } />
                        <LoginRoute path='/sos/audit'
                                    page={ DOSDefineAuditStartPageContainer } />
                        <LoginRoute path='/sos/audit/seed'
                                    page={ DOSDefineAuditSeedPageContainer } />
                        <LoginRoute path='/sos/audit/select-contests'
                                    page={ DOSDefineAduitSelectContestsPageContainer } />
                        <LoginRoute path='/sos/audit/review'
                                    page={ DOSDefineAuditReviewPageContainer } />
                        <LoginRoute path='/sos/contest'
                                    page={ DOSContestOverviewPageContainer } />
                        <LoginRoute path='/sos/contest/:contestId'
                                    page={ DOSContestDetailPageContainer } />
                        <LoginRoute path='/sos/county'
                                    page={ DOSCountyOverviewPageContainer } />
                        <LoginRoute path='/sos/county/:countyId'
                                    page={ DOSCountyDetailPageContainer } />
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
