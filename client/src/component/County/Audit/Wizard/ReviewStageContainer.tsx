import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/county/uploadAcvr';


interface ContainerProps {
    county: CountyState;
    currentBallot: Cvr;
    marks: Acvr;
    nextStage: OnClick;
    prevStage: OnClick;
}

class ReviewStageContainer extends React.Component<ContainerProps> {
    public render() {
        const props = { ...this.props, uploadAcvr };

        return <ReviewStage { ...props } />;
    }
}

function select(props: AppState) {
    const { county } = props;
    const { currentBallot } = county;

    const marks = county.acvrs[currentBallot.id];

    return { county, currentBallot, marks };
}


export default connect(select)(ReviewStageContainer);
