export default function selectContestsForAuditOk(
    state: DOS.AppState,
    action: Action.SelectContestsForAuditOk,
): DOS.AppState {
    const nextState = { ...state };

    nextState.contestsForAudit = action.data.sent;

    return nextState;
}
