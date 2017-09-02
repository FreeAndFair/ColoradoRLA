import * as React from 'react';
import { connect } from 'react-redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from 'corla/action/uploadAcvr';


class ReviewStageContainer extends React.Component<any, any> {
    public render() {
        const props = { ...this.props, uploadAcvr };

        return <ReviewStage { ...props } />;
    }
}


const mapStateToProps = ({ county }: any) => {
    const { currentBallot } = county;

    const marks = county.acvrs[currentBallot.id];

    return { county, currentBallot, marks };
};

export default connect(mapStateToProps)(ReviewStageContainer);
