import * as _ from 'lodash';


interface AppState {
    loggedIn: boolean;
    county?: any;
}

const defaultState = {
    loggedIn: false,
};


export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {
    case 'LOGIN':
        return { ...state, loggedIn: true };
    case 'FETCH_INITIAL_STATE_SEND':
        // TODO: add flag to indicate pending fetch.
        return state;
    case 'FETCH_INITIAL_STATE_RECEIVE':
        // TODO: should be a deep merge.
        return action.data;
    case 'UPDATE_BOARD_MEMBER': {
        const { index, name, party } = action.data;
        const nextState = { ...state };

        nextState.county.auditBoard = _.clone(nextState.county.auditBoard);
        nextState.county.auditBoard[index] = { name, party };

        return nextState;
    }
    case 'UPDATE_BALLOT_MARKS': {
        const { ballotId, choices, comments, contestId } = action.data;
        const nextState = { ...state };

        const ballots = _.clone(nextState.county.ballots);
        const ballotIndex = _.findIndex(ballots, (b: any) => b.id === ballotId);

        const ballot = { ...ballots[ballotIndex] };
        ballot.audited = true;
        const marks = { ...ballot.marks[contestId], choices, comments };
        ballot.marks[contestId] = marks;

        ballots[ballotIndex] = ballot;
        nextState.county.ballots = ballots

        return nextState;
    }
    default:
        return state;
    }
}
