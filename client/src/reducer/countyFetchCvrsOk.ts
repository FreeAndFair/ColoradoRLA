import { merge } from 'lodash';


const parseTimestamp = (ts: any): Date => {
    const t = (ts.seconds * 1000) + (ts.nanos / 1000 / 1000);

    const d = new Date();
    d.setTime(t);

    return d;
};

const parseContestInfo = (state: any) => (data: any) => {
    const { contest: contestId, choices } = data;

    const contest = state.county.contestDefs[contestId];

    return { choices, contest };
}


const parseCvr = (state: any) => (data: any) => ({
    ballotType: data.ballotType,
    batchId: data.batch_id,
    contestInfo: data.contest_info.map(parseContestInfo(state)),
    countyId: data.county_id,
    id: data.id,
    imprintedId: data.imprinted_id,
    recordId: data.record_id,
    recordType: data.record_type,
    scannerId: data.scanner_id,
    timestamp: parseTimestamp(data.timestamp),
});

const parse = (data: any, state: any) => data.map(parseCvr(state));


export default (state: any, action: any) => {
    if (!state.county.contestDefs) {
        return state;
    };

    const county = merge({}, state.county);

    county.cvrs = merge({}, parse(action.data, state));

    return merge({}, state, { county });
};
