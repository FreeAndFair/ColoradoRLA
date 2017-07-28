import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import BallotAuditStage from './BallotAuditStage';


class BallotAuditStageContainer extends React.Component<any, any> {
    public render() {
        return <BallotAuditStage { ...this.props } />;
    }
}

const mapStateToProps = ({ ballotStyles, county }: any) =>
    ({ ballotStyles, county });

const mapDispatchToProps = (dispatch: any) => bindActionCreators({}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(BallotAuditStageContainer);
