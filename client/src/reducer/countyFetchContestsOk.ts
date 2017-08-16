import { merge } from 'lodash';

export default (state: any, action: any) => {
    const county = merge({}, state.county);

    county.contests = merge({}, action.data);

    return merge({}, state, { county });
};
