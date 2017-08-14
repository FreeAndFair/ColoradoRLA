export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.sos.seed = action.sent.seed;

    return nextState;
};
