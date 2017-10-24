export default function uploadingCvrExport(
    state: County.AppState,
    action: Action.UploadingCvrExport,
): County.AppState {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState.uploadingCVRExport = uploading;

    return nextState;
}
