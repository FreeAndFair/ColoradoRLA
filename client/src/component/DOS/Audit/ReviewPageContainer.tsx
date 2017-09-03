import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import AuditReviewPage from './ReviewPage';

import publishBallotsToAudit from 'corla/action/dos/publishBallotsToAudit';


class AuditBallotListContainer extends React.Component<any, any> {
    public render() {
        const { history, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            back: () => history.push('/sos/audit/seed'),
            publishBallotsToAudit,
            saveAndDone: () => history.push('/sos'),
            sos,
        };

        return <AuditReviewPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => ({ sos });

export default connect(mapStateToProps)(AuditBallotListContainer);
