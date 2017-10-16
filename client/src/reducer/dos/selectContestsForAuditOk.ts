export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    nextState.sos.contestsForAudit = action.data.sent;

    return nextState;
};
