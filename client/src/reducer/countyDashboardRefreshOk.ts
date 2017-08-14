export default (state: any, action: any) => {
    const nextState = { ...state };

    const { data } = action;
    nextState.county = { ...state.county, ...data };

    return nextState;
};
