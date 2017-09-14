export default (state: any, action: any) => {
    const nextState = { ...state };

    const { uploading } = action.data;
    nextState.county.uploadingCvrExport = uploading;

    return nextState;
};
