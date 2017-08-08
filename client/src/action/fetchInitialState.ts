import { apiHost } from '../config';


function createInitialState(data: any) {
    const state = { ...data };

    return state;
}

export default function fetchInitialState(dispatch: any) {
    dispatch({ type: 'FETCH_INITIAL_STATE_SEND' });

    const fetchBallotStyles = fetch(`http://${apiHost}/ballot-style`)
        .then((res: any) => res.json());

    const fetchCastVoteRecords = fetch(`http://${apiHost}/cvr`)
        .then((res: any) => res.json());

    const fetchContests = fetch(`http://${apiHost}/contest`)
        .then((res: any) => res.json());

    Promise.all([fetchBallotStyles, fetchCastVoteRecords, fetchContests])
        .then((res: any) => {
            const [ballotStyles, castVoteRecords, contests] = res;

            const data = createInitialState({ ballotStyles, castVoteRecords, contests });

            dispatch({ data, type: 'FETCH_INITIAL_STATE_RECEIVE' });
        });
}
