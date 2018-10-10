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
        noMark,
    } = action.data;

    const nextMarks: any = {
        choices,
        comments,
    };

    nextMarks.noConsensus = _.isUndefined(noConsensus) ? false : !!noConsensus;
    nextMarks.noMark = _.isUndefined(noMark) ? false : !!noMark;

    const marks = nextState.acvrs![ballotId][contestId];

    // They clicked a choice
    if (_.size(nextMarks.choices) > _.size(marks.choices) && nextMarks.noMark) {
        nextMarks.noMark = false;
    // They clicked noMark
    } else if (nextMarks.noMark) {
        const toClear = _.merge({}, marks.choices, nextMarks.choices);
        nextMarks.choices = _.mapValues(toClear, () => false);
        // Mutually exclusive with noConsensus
        nextMarks.noConsensus = false;
    }

    if (nextMarks.noConsensus) {
        const toClear = _.merge({}, marks.choices, nextMarks.choices);
        nextMarks.choices = _.mapValues(toClear, () => false);
        // Mutually exclusive with noMark
        nextMarks.noMark = false;
    }

    nextState.acvrs![ballotId][contestId] = _.merge({}, marks, nextMarks);

    return nextState;
}
