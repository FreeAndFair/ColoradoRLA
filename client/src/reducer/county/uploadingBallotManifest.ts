export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState!.county!.uploadingBallotManifest = uploading;

    return nextState;
};
