import * as _ from 'lodash';

import countyAuthOk from './countyAuthOk';
import countyDashboardRefreshOk from './countyDashboardRefreshOk';
import dosAuthOk from './dosAuthOk';
import dosDashboardRefreshOk from './dosDashboardRefreshOk';
import selectContestsForAuditOk from './selectContestsForAuditOk';
import setRiskLimitOk from './setRiskLimitOk';


interface AppState {
    loggedIn: boolean;
    dashboard?: Dashboard;
    county?: any;
    sos?: any;
}

const defaultState = {
    loggedIn: false,
};


export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {

    case 'AUTH_COUNTY_ADMIN_OK': {
        return countyAuthOk(state);
    }

    case 'AUTH_STATE_ADMIN_OK': {
        return dosAuthOk(state);
    }

    case 'COUNTY_DASHBOARD_REFRESH_OK': {
        return countyDashboardRefreshOk(state, action);
    }

    case 'DOS_DASHBOARD_REFRESH_OK': {
        return dosDashboardRefreshOk(state, action);
    }

    case 'SELECT_CONTESTS_FOR_AUDIT_OK': {
        return selectContestsForAuditOk(state, action);
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

    case 'SET_RISK_LIMIT_OK': {
        return setRiskLimitOk(state, action);
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
        if (_.isEmpty(noConsensus)) {
            marks.noConsensus = !!noConsensus;
        }

        ballot.marks[contestId] = marks;

        ballots[ballotIndex] = ballot;
        nextState.county.ballots = ballots;

        return nextState;
    }

    default:
        return state;
    }
}
