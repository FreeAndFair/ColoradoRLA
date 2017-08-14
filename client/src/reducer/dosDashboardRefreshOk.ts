import formatDoSData from './formatDoSData';


export default (state: any, action: any) => {
    const nextState = { ...state };

    const { data } = action;
    nextState.sos = { ...state.sos, ...formatDoSData(data) };

    return nextState;
};
