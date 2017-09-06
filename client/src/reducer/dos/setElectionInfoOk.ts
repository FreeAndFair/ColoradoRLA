export default (state: any, action: any) => {
    const nextState = { ...state };

    const { sent } = action.data;
    nextState.sos.election = {
        date: sent.election_date,
        type: sent.election_type,
    };

    return nextState;
};
