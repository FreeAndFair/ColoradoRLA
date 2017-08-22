export default (state: any, action: any) => {
    const nextState = { ...state };

    const auditBoard = action.data.sent.map((e: any) => ({
        firstName: e.first_name,
        lastName: e.last_name,
        party: e.political_party,
    }));

    nextState.county.auditBoard = auditBoard;

    return nextState;
};
