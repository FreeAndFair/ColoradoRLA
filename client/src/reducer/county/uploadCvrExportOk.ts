import { parse } from 'corla/adapter/uploadCvrExport';


export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    nextState.county = { ...nextState.county, ...parse(action.data) };

    return nextState;
};
