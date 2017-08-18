import { merge } from 'lodash';


const parse = (data: any, state: any) => ({
        ballotType: data.ballot_type,
        batchId: data.batch_id,
        contestInfo: data.contest_info,
        countyId: data.county_id,
        id: data.id,
        imprintedId: data.imprinted_id,
        recordId: data.record_id,
        recordType: data.record_type,
        scannerId: data.scanner_id,
});


export default (state: any, action: any) => {
    const nextState = merge({}, state);

    const county = merge({}, state.county);
    county.currentBallot = parse(action.data, state);
    nextState.county = county;

    return nextState;
};
