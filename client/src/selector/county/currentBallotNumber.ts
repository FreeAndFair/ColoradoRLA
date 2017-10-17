import * as _ from 'lodash';


function currentBallotNumber(state: AppState): number {
    const { county } = state;
    const { cvrsToAudit, currentBallot } = county;

    if (!cvrsToAudit || !currentBallot) {
        return null;
    }

    return 1 + _.findIndex(cvrsToAudit, (cvr: any) => cvr.db_id === currentBallot.id);
}


export default currentBallotNumber;
