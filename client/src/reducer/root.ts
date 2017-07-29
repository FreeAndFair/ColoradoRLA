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

    case 'LOGIN': {
        return { ...state, loggedIn: true };
    }

    case 'FETCH_INITIAL_STATE_SEND': {
        // TODO: add flag to indicate pending fetch.
        return state;
    }

    case 'FETCH_INITIAL_STATE_RECEIVE': {
        // TODO: should be a deep merge.
        return action.data;
    }

    case 'SELECT_NEXT_BALLOT': {
        const nextState = { ...state };

        const { ballots, currentBallotId } = state.county;
        const currentIndex = _.findIndex(ballots, (b: any) => b.id === currentBallotId);
        const nextIndex = currentIndex + 1;

        if (nextIndex >= ballots.length) {
            // All ballots audited.
            // TODO: change audit status.
            return state;
        }

        const nextBallotId = ballots[nextIndex].id;
        nextState.county.currentBallotId = nextBallotId;

        return nextState;
    }

    case 'UPDATE_BOARD_MEMBER': {
        const { index, name, party } = action.data;
        const nextState = { ...state };

        nextState.county.auditBoard = _.clone(nextState.county.auditBoard);
        nextState.county.auditBoard[index] = { name, party };

        return nextState;
    }

    case 'UPDATE_BALLOT_MARKS': {
        const {
            ballotId,
            choices,
            comments,
            contestId,
            noConsensus,
        } = action.data;
        const nextState = { ...state };

        const ballots = _.clone(nextState.county.ballots);
        const ballotIndex = _.findIndex(ballots, (b: any) => b.id === ballotId);

        const ballot = { ...ballots[ballotIndex] };
        ballot.audited = true;

        const marks = { ...ballot.marks[contestId] };
        if (choices) {
            marks.choices = choices;
        }
        if (comments) {
            marks.comments = comments;
        }
        marks.noConsensus = !!noConsensus;

        ballot.marks[contestId] = marks;

        ballots[ballotIndex] = ballot;
        nextState.county.ballots = ballots;

        return nextState;
    }

    default:
        return state;
    }
}
