import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import BallotAuditStage from './BallotAuditStage';

import ballotNotFound from '../../../../action/ballotNotFound';

import currentBallotNumber from '../../../../selector/county/currentBallotNumber';


class BallotAuditStageContainer extends React.Component<any, any> {
    public render() {
        return <BallotAuditStage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;
    const { currentBallot } = county;

    return {
        county,
        currentBallot,
        currentBallotNumber: currentBallotNumber(state),
    };
};

const mapDispatchToProps = (dispatch: any) => bindActionCreators({
    updateBallotMarks: (data: any) => ({
        data,
        type: 'UPDATE_ACVR_FORM',
    }),
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(BallotAuditStageContainer);
