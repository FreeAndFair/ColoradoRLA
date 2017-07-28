import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import ReviewStage from './ReviewStage';

import findById from '../../../../findById';


class ReviewStageContainer extends React.Component<any, any> {
    public render() {
        return <ReviewStage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { ballotStyles, county } = state;

    const currentBallot = findById(county.ballots, county.currentBallotId);

    return {
        ballotStyles,
        county,
        currentBallot,
        marks: currentBallot.marks,
    };
};

const mapDispatchToProps = (dispatch: any) => bindActionCreators({}, dispatch);


export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ReviewStageContainer);
