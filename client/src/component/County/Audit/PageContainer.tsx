import * as React from 'react';
import { Redirect } from 'react-router-dom';

import withPoll from 'corla/component/withPoll';

import EndOfRoundPageContainer from './EndOfRound/PageContainer';
import CountyAuditPage from './Page';

import notice from 'corla/notice';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import auditCompleteSelector from 'corla/selector/county/auditComplete';
import canAuditSelector from 'corla/selector/county/canAudit';
import roundInProgressSelector from 'corla/selector/county/roundInProgress';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (this.props.auditComplete) {
            notice.ok('The audit is complete.');

            return <Redirect to={ '/county' } />;
        }

        if (!this.props.canAudit) {
            return <Redirect to={ '/county' } />;
        }

        if (this.props.showEndOfRoundPage) {
            return <EndOfRoundPageContainer />;
        }

        return <CountyAuditPage />;
    }
}

const select = (state: any) => {
    const showEndOfRoundPage = allRoundsCompleteSelector(state)
                            || !roundInProgressSelector(state);

    return {
        auditComplete: auditCompleteSelector(state),
        canAudit: canAuditSelector(state),
        showEndOfRoundPage,
    };
};


export default withPoll(
    CountyAuditContainer,
    'COUNTY_AUDIT_POLL_START',
    'COUNTY_AUDIT_POLL_STOP',
    select,
);
