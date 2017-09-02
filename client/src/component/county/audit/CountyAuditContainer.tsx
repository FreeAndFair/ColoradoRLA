import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import CountyAuditPage from './CountyAuditPage';
import EndOfRoundPageContainer from './EndOfRoundPageContainer';

import notice from 'corla/notice';

import allRoundsComplete from 'corla/selector/county/allRoundsComplete';
import auditComplete from 'corla/selector/county/auditComplete';
import canAudit from 'corla/selector/county/canAudit';
import roundInProgress from 'corla/selector/county/roundInProgress';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (!this.props.canAudit) {
            notice.danger('Not ready to begin audit.');

            return <Redirect to={ '/county' } />;
        }

        if (this.props.auditComplete) {
            notice.ok('The audit is complete.');

            return <Redirect to={ '/county' } />;
        }

        if (this.props.showEndOfRoundPage) {
            return <EndOfRoundPageContainer />;
        }

        return <CountyAuditPage />;
    }
}

const mapStateToProps = (state: any) => {
    const showEndOfRoundPage = (!roundInProgress(state) && !allRoundsComplete(state))
                            || allRoundsComplete(state);

    return {
        auditComplete: auditComplete(state),
        canAudit: canAudit(state),
        showEndOfRoundPage,
    };
};


export default connect(mapStateToProps)(CountyAuditContainer);
