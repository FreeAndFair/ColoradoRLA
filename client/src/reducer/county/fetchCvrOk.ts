import { forEach, merge } from 'lodash';


function createEmptyAcvr(cvr: Cvr): Acvr {
    const acvr: any = {};

    forEach(cvr.contestInfo, c => {
        acvr[c.contest] = {
            choices: {},
            comments: '',
        };
    });

    return acvr;
}

const parse = (data: CvrJson, state: AppState): Cvr => ({
    ballotType: data.ballot_type,
    batchId: data.batch_id,
    contestInfo: data.contest_info,
    countyId: data.county_id,
    cvrNumber: data.cvr_number,
    id: data.id,
    imprintedId: data.imprinted_id,
    recordId: data.record_id,
    recordType: data.record_type,
    scannerId: data.scanner_id,
});


export default (state: AppState, action: any): AppState => {
    const nextState = merge({}, state);

    if (!state.county) { return nextState; }

    const county = merge({}, state.county);
    const currentBallot = parse(action.data, state);
    county.currentBallot = currentBallot;

    if (!county.acvrs![currentBallot.id]) {
        county.acvrs![currentBallot.id] = createEmptyAcvr(currentBallot);
    }
    nextState.county = county;

    return nextState;
};
