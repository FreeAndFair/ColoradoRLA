import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/county/uploadAcvr';


interface ContainerProps {
    countyState: County.AppState;
    currentBallot: Cvr;
    marks: County.Acvr;
    nextStage: OnClick;
    prevStage: OnClick;
}

class ReviewStageContainer extends React.Component<ContainerProps> {
    public render() {
        const props = { ...this.props, uploadAcvr };

        return <ReviewStage { ...props } />;
    }
}

function select(countyState: County.AppState) {
    const { currentBallot } = countyState;

    if (!currentBallot) { return {}; }

    const marks = countyState!.acvrs![currentBallot.id];

    return { countyState, currentBallot, marks };
}


export default connect(select)(ReviewStageContainer);
