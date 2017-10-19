export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    if (!nextState.sos) { return nextState; }

    nextState.sos.seed = action.data.sent.seed;

    return nextState;
};
