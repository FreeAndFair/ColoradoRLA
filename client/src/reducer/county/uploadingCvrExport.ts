export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState.county.uploadingCvrExport = uploading;

    return nextState;
};
