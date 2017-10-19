import * as React from 'react';

import { History } from 'history';

import withPoll from 'corla/component/withPoll';

import counties from 'corla/data/counties';

import MissedDeadlinePage from './MissedDeadlinePage';
import CountyDashboardPage from './Page';

import finishAudit from 'corla/action/county/finishAudit';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import auditBoardSignedInSelector from 'corla/selector/county/auditBoardSignedIn';
import auditCompleteSelector from 'corla/selector/county/auditComplete';
import auditStartedSelector from 'corla/selector/county/auditStarted';
import canAuditSelector from 'corla/selector/county/canAudit';
import canRenderReportSelector from 'corla/selector/county/canRenderReport';
import canSignInSelector from 'corla/selector/county/canSignIn';
import currentRoundNumberSelector from 'corla/selector/county/currentRoundNumber';
import missedDeadlineSelector from 'corla/selector/county/missedDeadline';


interface DashboardProps {
    allRoundsComplete: boolean;
    auditBoardSignedIn: boolean;
    auditComplete: boolean;
    auditStarted: boolean;
    canAudit: boolean;
    canRenderReport: boolean;
    canSignIn: boolean;
    contests: County.ContestDefs;
    county: County.AppState;
    currentRoundNumber: number;
    history: History;
    missedDeadline: boolean;
}

class CountyDashboardContainer extends React.Component<DashboardProps> {
    public render() {
        const {
            allRoundsComplete,
            auditStarted,
            canAudit,
            canRenderReport,
            canSignIn,
            county,
            history,
            missedDeadline,
        } = this.props;

        if (!county) {
            return <div />;
        }

        if (missedDeadline) {
            return <MissedDeadlinePage />;
        }

        const countyInfo = county.id ? counties[county.id] : {};
        const boardSignIn = () => history.push('/county/board');
        const startAudit = () => history.push('/county/audit');

        const props = {
            allRoundsComplete,
            auditStarted,
            boardSignIn,
            canAudit,
            canRenderReport,
            canSignIn,
            countyInfo,
            finishAudit,
            startAudit,
            ...this.props,
        };

        return <CountyDashboardPage { ...props } />;
    }
}

const select = (state: AppState) => {
    const { county } = state;

    if (!county) {
        return {};
    }

    const { contestDefs } = county;

    return {
        allRoundsComplete: allRoundsCompleteSelector(state),
        auditBoardSignedIn: auditBoardSignedInSelector(state),
        auditComplete: auditCompleteSelector(state),
        auditStarted: auditStartedSelector(state),
        canAudit: canAuditSelector(state),
        canRenderReport: canRenderReportSelector(state),
        canSignIn: canSignInSelector(state),
        contests: contestDefs,
        county,
        currentRoundNumber: currentRoundNumberSelector(state),
        missedDeadline: missedDeadlineSelector(state),
    };
};

export default withPoll(
    CountyDashboardContainer,
    'COUNTY_DASHBOARD_POLL_START',
    'COUNTY_DASHBOARD_POLL_STOP',
    select,
);
