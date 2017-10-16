export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    nextState.sos.seed = action.data.sent.seed;

    return nextState;
};
