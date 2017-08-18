import * as _ from 'lodash';


export default (state: any, action: any) => {
    const nextState: any = _.merge({}, state);
    const { county } = nextState;

    const {
        ballotId,
        choices,
        comments,
        contestId,
        noConsensus,
    } = action.data;

    if (!nextState.county.acvrs) {
        nextState.county.acvrs = {};
    }

    if (!nextState.county.acvrs[ballotId]) {
        nextState.county.acvrs[ballotId] = {};
    }

    if (!nextState.county.acvrs[ballotId][contestId]) {
        nextState.county.acvrs[ballotId][contestId] = {};
    }

    const nextMarks: any = {
        choices,
        comments,
    };

    if (!_.isUndefined(noConsensus)) {
        nextMarks.noConsensus = !!noConsensus;
    }

    const marks = nextState.county.acvrs[ballotId][contestId];
    nextState.county.acvrs[ballotId][contestId] = _.merge({}, marks, nextMarks);

    return nextState;
}
