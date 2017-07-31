import * as React from 'react';
import { connect } from 'react-redux';

import AuditBallotListPage from './AuditBallotListPage';


class AuditBallotListContainer extends React.Component<any, any> {
    public render() {
        return <AuditBallotListPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditBallotListContainer);
