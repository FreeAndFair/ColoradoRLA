import { parse } from 'corla/adapter/uploadCvrExport';


export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.county = { ...nextState.county, ...parse(action.data) };

    return nextState;
};
