import * as _ from 'lodash';


export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    nextState.county.cvrsToAudit = action.data;

    return nextState;
};
