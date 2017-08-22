import * as React from 'react';
import { connect } from 'react-redux';

import AuditReviewPage from './AuditReviewPage';

import publishBallotsToAudit from '../../../action/publishBallotsToAudit';


class AuditBallotListContainer extends React.Component<any, any> {
    public render() {
        const { history, sos } = this.props;

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
