import { parse } from '../adapter/uploadCvrExport';


export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.county = { ...nextState.county, ...parse(action.sent) };

    return nextState;
};
