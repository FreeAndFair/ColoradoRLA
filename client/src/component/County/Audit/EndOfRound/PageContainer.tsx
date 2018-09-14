import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundPage from './Page';

import allRoundsCompleteSelector from 'corla/selector/county/allRoundsComplete';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import currentRoundNumberSelector from 'corla/selector/county/currentRoundNumber';
import previousRoundSelector from 'corla/selector/county/previousRound';


function signedOff(auditBoardIndex: number, round: Round): boolean {
    if (!round.signatories) {
        return false;
    }

    if (!round.signatories[auditBoardIndex]) {
        return false;
    }

    if (round.signatories[auditBoardIndex].length < 2) {
        return false;
    }

    return true;
}

interface ContainerProps {
    allRoundsComplete: boolean;
    areAuditBoardsDone: boolean;
    auditBoardIndex: number;
    countyInfo: CountyInfo;
    currentRoundNumber: number;
    election: Election;
    estimatedBallotsToAudit: number;
    isAuditBoardDone: boolean;
    previousRound: Round;
    previousRoundSignedOff: boolean;
}

class EndOfRoundPageContainer extends React.Component<ContainerProps> {
    public render() {
        return <EndOfRoundPage { ...this.props } />;
    }
}

function select(countyState: County.AppState) {
    // TODO: No great way to handle this error.
    // Note that 0 is falsey in JS, so this still works for a valid audit board
    // index of 0.
    const auditBoardIndex = countyState.auditBoardIndex || 0;
    const previousRound = previousRoundSelector(countyState);
    const previousRoundSignedOff = previousRound && signedOff(auditBoardIndex, previousRound);

    return {
        allRoundsComplete: allRoundsCompleteSelector(countyState),
        auditBoardIndex,
        countyInfo: countyInfoSelector(countyState),
        currentRoundNumber: currentRoundNumberSelector(countyState),
        election: countyState.election,
        estimatedBallotsToAudit: countyState.estimatedBallotsToAudit,
        previousRound: previousRound || {},
        previousRoundSignedOff: previousRound ? signedOff(auditBoardIndex, previousRound) : false,
    };
}


export default connect(select)(EndOfRoundPageContainer);
