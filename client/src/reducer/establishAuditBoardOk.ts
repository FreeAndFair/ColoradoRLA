export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.county.auditBoard = action.data;

    return nextState;
};
