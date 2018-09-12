import * as React from 'react';
import { connect } from 'react-redux';

import BallotAuditStage from './BallotAuditStage';

import action from 'corla/action';

import ballotNotFound from 'corla/action/county/ballotNotFound';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';
import totalBallotsForBoard from 'corla/selector/county/totalBallotsForBoard';


interface ContainerProps {
    auditBoardIndex: number;
    countyState: County.AppState;
    currentBallot: County.CurrentBallot;
    currentBallotNumber: number;
    nextStage: OnClick;
    prevStage: OnClick;
    totalBallotsForBoard: number;
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

function select(countyState: County.AppState) {
    const { currentBallot } = countyState;

    return {
        auditBoardIndex: countyState.auditBoardIndex,
        countyState,
        currentBallot,
        currentBallotNumber: currentBallotNumber(countyState),
        totalBallotsForBoard: totalBallotsForBoard(countyState),
    };
}


export default connect(select)(BallotAuditStageContainer);
