import * as React from 'react';
import { connect } from 'react-redux';

import AuditBallotListPage from './AuditBallotListPage';


class AuditBallotListContainer extends React.Component<any, any> {
    public render() {
        const back = () => this.props.history.push('/sos/audit/seed');
        const saveAndDone = () => this.props.history.push('/sos');

        return <AuditBallotListPage { ...{ back, saveAndDone} } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditBallotListContainer);
