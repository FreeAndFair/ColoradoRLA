import * as _ from 'lodash';


function currentBallotNumber(state: County.AppState): Option<number> {
    const { auditBoardIndex,
            ballotSequenceAssignment,
            cvrsToAudit,
            currentBallot } = state;

    // Project an audit-board-specific view of the overall CVRs to audit.
    const auditBoardSlice = () => {
        const bsa: any = _.nth(ballotSequenceAssignment, auditBoardIndex);

        if (!bsa) {
          return [];
        }

        const { index, count } = bsa;

        return _.slice(cvrsToAudit, index, index + count);
    };

    if (typeof auditBoardIndex != "number") { return null; }
    if (!ballotSequenceAssignment) { return null; }
    if (!cvrsToAudit) { return null; }
    if (!currentBallot) { return null; }

    return 1 + _.findIndex(auditBoardSlice(), cvr => {
        return cvr.db_id === currentBallot.id;
    });
}


export default currentBallotNumber;
