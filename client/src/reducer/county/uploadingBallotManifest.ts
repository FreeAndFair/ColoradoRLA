export default (state: any, action: any) => {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState.county.uploadingBallotManifest = uploading;

    return nextState;
};
