import { parse } from '../adapter/setRiskLimit';


export default (state: any, action: any) => {
    const nextState = { ...state };

    const { sent } = action.data;
    nextState.sos = { ...state.sos, ...parse(sent) };

    return nextState;
};
