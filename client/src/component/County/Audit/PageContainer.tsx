import * as React from 'react';
import { Redirect } from 'react-router-dom';

import withCountyState from 'corla/component/withCountyState';
import withPoll from 'corla/component/withPoll';

import EndOfRoundPageContainer from './EndOfRound/PageContainer';
import CountyAuditPage from './Page';

import notice from 'corla/notice';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import auditCompleteSelector from 'corla/selector/county/auditComplete';
import canAuditSelector from 'corla/selector/county/canAudit';
import roundInProgressSelector from 'corla/selector/county/roundInProgress';


interface ContainerProps {
    auditComplete: boolean;
    canAudit: boolean;
    showEndOfRoundPage: boolean;
}

class CountyAuditContainer extends React.Component<ContainerProps> {
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

function select(countyState: County.AppState) {
    const showEndOfRoundPage = allRoundsCompleteSelector(countyState)
                            || !roundInProgressSelector(countyState);

    return {
        auditComplete: auditCompleteSelector(countyState),
        canAudit: canAuditSelector(countyState),
        showEndOfRoundPage,
    };
}


export default withPoll(
    withCountyState(CountyAuditContainer),
    'COUNTY_AUDIT_POLL_START',
    'COUNTY_AUDIT_POLL_STOP',
    select,
);
