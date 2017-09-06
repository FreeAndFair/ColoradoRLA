import * as React from 'react';
import { connect } from 'react-redux';

import EndOfLastRoundPage from './LastRoundPage';
import EndOfRoundPage from './Page';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import currentRoundSelector from 'corla/selector/county/currentRound';
import previousRoundSelector from 'corla/selector/county/previousRound';


function signedOff(round: any): boolean {
    if (!round.signatories) {
        return false;
    }

    if (round.signatories.length < 2) {
        return false;
    }

    return true;
}

class EndOfRoundPageContainer extends React.Component<any, any> {
    public render() {
        return <EndOfRoundPage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    const previousRound = previousRoundSelector(state);

    return {
        allRoundsComplete: allRoundsCompleteSelector(state),
        countyInfo: countyInfoSelector(state),
        currentRound: currentRoundSelector(state),
        election: state.county.election,
        estimatedBallotsToAudit: state.county.estimatedBallotsToAudit,
        previousRound,
        previousRoundSignedOff: signedOff(previousRound),
    };
};


export default connect(mapStateToProps)(EndOfRoundPageContainer);
