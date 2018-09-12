import * as _ from 'lodash';


function totalBallotsForBoard(state: County.AppState): Option<number> {
    const { auditBoardIndex, ballotSequenceAssignment } = state;

    if (typeof auditBoardIndex != "number") { return null; }
    if (!ballotSequenceAssignment) { return null; }

    const bsa: any = _.nth(ballotSequenceAssignment, auditBoardIndex);

    const { count } = bsa;

    return count;
}


export default totalBallotsForBoard;
