import * as _ from 'lodash';


function currentBallotNumber(state: any): number {
    const { county } = state;
    const { cvrsToAudit, currentBallot } = county;

    if (!cvrsToAudit || !currentBallot) {
        return null;
    }

    const unaudited = _.filter(cvrsToAudit, (cvr: any) => !cvr.audited);

    return 1 + _.findIndex(unaudited, (cvr: any) => cvr.db_id === currentBallot.id);
}


export default currentBallotNumber;
