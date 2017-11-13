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

function select(countyState: County.AppState) {
    const previousRound = previousRoundSelector(countyState);
    const previousRoundSignedOff = previousRound && signedOff(previousRound);

    return {
        allRoundsComplete: allRoundsCompleteSelector(countyState),
        countyInfo: countyInfoSelector(countyState),
        currentRoundNumber: currentRoundNumberSelector(countyState),
        election: countyState.election,
        estimatedBallotsToAudit: countyState.estimatedBallotsToAudit,
        previousRound: previousRound || {},
        previousRoundSignedOff: previousRound ? signedOff(previousRound) : false,
    };
}


export default connect(select)(EndOfRoundPageContainer);
