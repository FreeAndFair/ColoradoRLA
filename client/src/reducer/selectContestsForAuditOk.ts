export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.sos.contestsForAudit = action.data.sent;

    return nextState;
};
