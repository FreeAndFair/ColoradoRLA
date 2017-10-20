export default function uploadRandomSeedOk(
    state: DOS.AppState,
    action: Action.UploadRandomSeedOk,
): DOS.AppState {
    const nextState = { ...state };

    nextState.seed = action.data.sent.seed;

    return nextState;
}
