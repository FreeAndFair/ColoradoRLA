import * as React from 'react';
import { Provider, Store } from 'react-redux';
import {
    BrowserRouter as Router,
    Redirect,
    Route,
    Switch,
} from 'react-router-dom';

import RootRedirectContainer from './RootRedirectContainer';

import CountyContestDetailContainer from './county/ContestDetailContainer';
import CountyContestOverviewContainer from './county/ContestOverviewContainer';

import CountyAuditContainer from './county/audit/CountyAuditContainer';
import CountyHomeContainer from './county/home/CountyHomeContainer';

import GlossaryContainer from './help/GlossaryContainer';
import HelpRootContainer from './help/HelpRootContainer';
import ManualContainer from './help/ManualContainer';

import LoginContainer from './login/LoginContainer';

import AuditBallotListContainer from './sos/audit/AuditBallotListContainer';
import AuditContainer from './sos/audit/AuditContainer';
import AuditSeedContainer from './sos/audit/AuditSeedContainer';
import SelectContestsPageContainer from './sos/audit/SelectContestsPageContainer';

import ContestDetailContainer from './sos/contest/ContestDetailContainer';
import ContestOverviewContainer from './sos/contest/ContestOverviewContainer';
import CountyDetailContainer from './sos/county/CountyDetailContainer';
import CountyOverviewContainer from './sos/county/CountyOverviewContainer';

import SoSHomeContainer from './sos/SoSHomeContainer';


export interface RootContainerProps {
    store: Store<any>;
}

const LoginRoute = ({ store, page: Page, ...rest }: any) => {
    const render = (props: any) => {
        const { loggedIn } = store.getState();

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

type RouteDef = [string, React.ComponentClass];

const makeRoute = (store: any) => (def: RouteDef) => {
    const [path, Page] = def;
    return (
        <LoginRoute
            exact
            key={ path }
            path={ path }
            page={ Page }
            store={ store }
        />
    );
};

const routes: RouteDef[] = [
    ['/', RootRedirectContainer],
    ['/county', CountyHomeContainer],
    ['/county/audit', CountyAuditContainer],
    ['/county/contest', CountyContestOverviewContainer],
    ['/county/contest/:contestId', CountyContestDetailContainer],
    ['/help', HelpRootContainer],
    ['/help/glossary', GlossaryContainer],
    ['/help/manual', ManualContainer],
    ['/sos', SoSHomeContainer],
    ['/sos/audit', AuditContainer],
    ['/sos/audit/seed', AuditSeedContainer],
    ['/sos/audit/select-contests', SelectContestsPageContainer],
    ['/sos/audit/ballots', AuditBallotListContainer],
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
                        <Route exact path='/login' component={ LoginContainer } />
                        { routes.map(makeRoute(store)) }
                    </Switch>
                </Router>
            </Provider>
        );
    }
}
