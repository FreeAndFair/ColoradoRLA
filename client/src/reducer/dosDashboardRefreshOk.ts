import { merge } from 'lodash';

import { parse } from '../adapter/dosDashboardRefresh';


export default (state: any, action: any) => {
    const sos = merge({}, parse(action.data));

    return merge({}, state, { sos });
};
