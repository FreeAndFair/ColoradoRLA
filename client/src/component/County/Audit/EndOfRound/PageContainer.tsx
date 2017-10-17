import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundPage from './Page';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import currentRoundNumberSelector from 'corla/selector/county/currentRoundNumber';
import previousRoundSelector from 'corla/selector/county/previousRound';


function signedOff(round: Round): boolean {
    if (!round.signatories) {
        return false;
    }

    if (round.signatories.length < 2) {
        return false;
    }

    return true;
}

interface ContainerProps {
    allRoundsComplete: boolean;
    countyInfo: CountyInfo;
    currentRoundNumber: number;
    election: Election;
    estimatedBallotsToAudit: number;
    previousRound: Round;
    previousRoundSignedOff: boolean;
}

class EndOfRoundPageContainer extends React.Component<ContainerProps> {
    public render() {
        return <EndOfRoundPage { ...this.props } />;
    }
}

function select(state: AppState) {
    const { county } = state;

    const previousRound = previousRoundSelector(state);
    const previousRoundSignedOff = previousRound && signedOff(previousRound);

    return {
        allRoundsComplete: allRoundsCompleteSelector(state),
        countyInfo: countyInfoSelector(state),
        currentRoundNumber: currentRoundNumberSelector(state),
        election: state.county.election,
        estimatedBallotsToAudit: state.county.estimatedBallotsToAudit,
        previousRound: previousRound || {},
        previousRoundSignedOff: signedOff(previousRound),
    };
}


export default connect(select)(EndOfRoundPageContainer);
