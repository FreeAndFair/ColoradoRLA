import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/county/uploadAcvr';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';
import totalBallotsForBoard from 'corla/selector/county/totalBallotsForBoard';


interface ContainerProps {
    countyState: County.AppState;
    currentBallot?: County.CurrentBallot;
    currentBallotNumber: number;
    marks?: County.ACVR;
    nextStage: OnClick;
    prevStage: OnClick;
    totalBallotsForBoard: number;
}

class ReviewStageContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            countyState,
            currentBallot,
            currentBallotNumber,
            marks,
            nextStage,
            prevStage,
            totalBallotsForBoard,
        } = this.props;

        if (!currentBallot) {
            return null;
        }

        if (!marks) {
            return null;
        }

        return <ReviewStage countyState={ countyState }
                            currentBallot={ currentBallot }
                            currentBallotNumber={ currentBallotNumber }
                            marks={ marks }
                            nextStage={ nextStage }
                            prevStage={ prevStage }
                            totalBallotsForBoard={ totalBallotsForBoard }
                            uploadAcvr={ uploadAcvr } />;
    }
}

function select(countyState: County.AppState) {
    const { currentBallot } = countyState;

    if (!currentBallot) {
        return { countyState };
    }

    const marks = countyState.acvrs[currentBallot.id];

    return {
      countyState,
      currentBallot,
      currentBallotNumber: currentBallotNumber(countyState),
      marks,
      totalBallotsForBoard: totalBallotsForBoard(countyState),
    };
}


export default connect(select)(ReviewStageContainer);
