import * as _ from 'lodash';


function canSignIn(state: AppState) {
    if (!_.has(state, 'county.asm.county.currentState')) {
        return false;
    }

    const { currentState } = state.county.asm.county;

    return currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canSignIn;
