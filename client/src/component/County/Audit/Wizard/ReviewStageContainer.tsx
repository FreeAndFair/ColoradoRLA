import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/county/uploadAcvr';


interface ContainerProps {
    countyState: County.AppState;
    currentBallot?: County.CurrentBallot;
    marks?: County.ACVR;
    nextStage: OnClick;
    prevStage: OnClick;
}

class ReviewStageContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            countyState,
            currentBallot,
            marks,
            nextStage,
            prevStage,
        } = this.props;

        if (!currentBallot) {
            return null;
        }

        if (!marks) {
            return null;
        }

        return <ReviewStage countyState={ countyState }
                            currentBallot={ currentBallot }
                            marks={ marks }
                            nextStage={ nextStage }
                            prevStage={ prevStage }
                            uploadAcvr={ uploadAcvr } />;
    }
}

function select(countyState: County.AppState) {
    const { currentBallot } = countyState;

    if (!currentBallot) {
        return { countyState };
    }

    const marks = countyState.acvrs[currentBallot.id];

    return { countyState, currentBallot, marks };
}


export default connect(select)(ReviewStageContainer);
