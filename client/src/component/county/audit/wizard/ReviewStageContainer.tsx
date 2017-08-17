import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import ReviewStage from './ReviewStage';

import uploadAcvr from '../../../../action/uploadAcvr';


class ReviewStageContainer extends React.Component<any, any> {
    public render() {
        return <ReviewStage { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    const { ballots, currentBallot } = county;

    const marks = county.acvr[currentBallot.id];

    return { county, currentBallot, marks };
};

const mapDispatchToProps = (dispatch: any) => bindActionCreators({
    selectNextBallot: () => ({ type: 'SELECT_NEXT_BALLOT' }),
    uploadAcvr,
}, dispatch);


export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ReviewStageContainer);
