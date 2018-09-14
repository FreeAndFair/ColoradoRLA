import * as _ from 'lodash';


function areAuditBoardsDone(state: County.AppState): boolean {
    const { ballotUnderAuditIds } = state;

    if (!ballotUnderAuditIds) { return false; }

    return _.every(ballotUnderAuditIds, (id) => id == null);
}


export default areAuditBoardsDone;
