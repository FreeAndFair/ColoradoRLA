import { merge } from 'lodash';


export default (state: any, action: any) => {
    const nextState = merge({}, state);

    return nextState;
};
