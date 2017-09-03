import * as React from 'react';
import { connect } from 'react-redux';

import BallotAuditStage from './BallotAuditStage';

import action from 'corla/action';

import ballotNotFound from 'corla/action/ballotNotFound';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';


class BallotAuditStageContainer extends React.Component<any, any> {
    public render() {
        const props = {
            ...this.props,
            updateBallotMarks: (data: any) => action('UPDATE_ACVR_FORM', data),
        };

        return <BallotAuditStage { ...props } />;
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


export default connect(mapStateToProps)(BallotAuditStageContainer);
