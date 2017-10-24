export default function uploadingBallotManifest(
    state: County.AppState,
    action: Action.UploadingBallotManifest,
): County.AppState {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState.uploadingBallotManifest = uploading;

    return nextState;
}
