function createInitialState(data: any) {
    const state = { ...data };

    return state;
}

export default function fetchInitialState(dispatch: any) {
    dispatch({ type: 'FETCH_INITIAL_STATE_SEND' });

    const fetchBallotStyles = fetch('http://localhost:4000/ballot-style')
        .then((res: any) => res.json());

    const fetchCastVoteRecords = fetch('http://localhost:4000/cvr')
        .then((res: any) => res.json());

    const fetchContests = fetch('http://localhost:4000/contest')
        .then((res: any) => res.json());

    Promise.all([fetchBallotStyles, fetchCastVoteRecords, fetchContests])
        .then((res: any) => {
            const [ballotStyles, castVoteRecords, contests] = res;

            const data = createInitialState({ ballotStyles, castVoteRecords, contests });

            dispatch({ data, type: 'FETCH_INITIAL_STATE_RECEIVE' });
        });
}
