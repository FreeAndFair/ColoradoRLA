import * as _ from 'lodash';


export default function updateAcvrForm(
    state: County.AppState,
    action: Action.UpdateAcvrForm,
): County.AppState {
    const nextState = _.merge({}, state);

    const {
        ballotId,
        choices,
        comments,
        contestId,
        noConsensus,
    } = action.data;

    const nextMarks: any = {
        choices,
        comments,
    };

    if (!_.isUndefined(noConsensus)) {
        nextMarks.noConsensus = !!noConsensus;
    }

    const marks = nextState.acvrs![ballotId][contestId];

    if (nextMarks.noConsensus) {
        const toClear = _.merge({}, marks.choices, nextMarks.choices);
        nextMarks.choices = _.mapValues(toClear, () => false);
    }

    nextState.acvrs![ballotId][contestId] = _.merge({}, marks, nextMarks);

    return nextState;
}
