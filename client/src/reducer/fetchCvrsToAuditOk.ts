import * as _ from 'lodash';


export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.county.cvrsToAudit = action.data;

    return nextState;
};
