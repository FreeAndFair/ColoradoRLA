import * as _ from 'lodash';


export default (state: any, action: any) => {
    const nextState: any = _.merge({}, state);

    const {
        ballotId,
        choices,
        comments,
        contestId,
        noConsensus,
    } = action.data;

    const acvr = {
        [contestId]: {
            choices,
            comments,
            noConsensus,
        },
    };

    const acvrs = { [ballotId]: acvr };
    nextState.county.acvrs = _.merge({}, state.county.acvrs, acvrs);

    return nextState;
}
