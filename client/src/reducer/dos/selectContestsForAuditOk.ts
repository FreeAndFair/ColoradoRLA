export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    if (!nextState.sos) { return nextState; }

    nextState.sos.contestsForAudit = action.data.sent;

    return nextState;
};
