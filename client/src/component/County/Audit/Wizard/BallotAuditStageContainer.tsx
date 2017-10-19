import * as React from 'react';
import { connect } from 'react-redux';

import BallotAuditStage from './BallotAuditStage';

import action from 'corla/action';

import ballotNotFound from 'corla/action/county/ballotNotFound';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';


interface ContainerProps {
    county: County.AppState;
    currentBallot: Cvr;
    currentBallotNumber: number;
    nextStage: OnClick;
    prevStage: OnClick;
}

class BallotAuditStageContainer extends React.Component<ContainerProps> {
    public render() {
        const props = {
            ...this.props,
            updateBallotMarks: (data: any) => action('UPDATE_ACVR_FORM', data),
        };

        return <BallotAuditStage { ...props } />;
    }
}

function select(state: AppState) {
    const { county } = state;
    const { currentBallot } = county!;

    return {
        county,
        currentBallot,
        currentBallotNumber: currentBallotNumber(state),
    };
}


export default connect(select)(BallotAuditStageContainer);
