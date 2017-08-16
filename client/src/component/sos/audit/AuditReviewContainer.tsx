import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import AuditReviewPage from './AuditReviewPage';

import publishBallotsToAudit from '../../../action/publishBallotsToAudit';


class AuditBallotListContainer extends React.Component<any, any> {
    public render() {
        const { history, publishBallotsToAudit } = this.props;

        const props = {
            back: () => history.push('/sos/audit/seed'),
            publishBallotsToAudit,
            saveAndDone: () => history.push('/sos'),
        };

        return <AuditReviewPage { ...props } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    publishBallotsToAudit,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditBallotListContainer);
