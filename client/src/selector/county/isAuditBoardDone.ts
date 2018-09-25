import * as _ from 'lodash';


function isAuditBoardDone(state: County.AppState): boolean {
    const { auditBoardIndex,
            ballotUnderAuditIds } = state;

    if (typeof auditBoardIndex !== 'number') { return false; }
    if (!ballotUnderAuditIds) { return false; }

    return _.nth(ballotUnderAuditIds, auditBoardIndex) == null;
}


export default isAuditBoardDone;
