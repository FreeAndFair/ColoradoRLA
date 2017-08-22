export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.sos.seed = action.data.sent.seed;

    return nextState;
};
