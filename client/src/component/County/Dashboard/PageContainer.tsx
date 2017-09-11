import * as React from 'react';
import { connect } from 'react-redux';

import counties from 'corla/data/counties';

import CountyDashboardPage from './Page';

import finishAudit from 'corla/action/county/finishAudit';

import allRoundsComplete from 'corla/selector/county/allRoundsComplete';
import auditBoardSignedIn from 'corla/selector/county/auditBoardSignedIn';
import auditComplete from 'corla/selector/county/auditComplete';
import auditStarted from 'corla/selector/county/auditStarted';
import canAudit from 'corla/selector/county/canAudit';
import canRenderReport from 'corla/selector/county/canRenderReport';
import canSignIn from 'corla/selector/county/canSignIn';


class CountyDashboardContainer extends React.Component<any, any> {
    public render() {
        const {
            allRoundsComplete,
            auditStarted,
            canAudit,
            canRenderReport,
            canSignIn,
            county,
            history,
        } = this.props;

        const countyInfo = county.id ? counties[county.id] : {};
        const boardSignIn = () => history.push('/county/sign-in');
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

const mapStateToProps = (state: any) => {
    const { county } = state;
    const { contestDefs } = county;

    return {
        allRoundsComplete: allRoundsComplete(state),
        auditBoardSignedIn: auditBoardSignedIn(state),
        auditComplete: auditComplete(state),
        auditStarted: auditStarted(state),
        canAudit: canAudit(state),
        canRenderReport: canRenderReport(state),
        canSignIn: canSignIn(state),
        contests: contestDefs,
        county,
    };
};

export default connect(mapStateToProps)(CountyDashboardContainer);
