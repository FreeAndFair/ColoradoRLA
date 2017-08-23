import * as _ from 'lodash';


interface ContestInfo {
    choices: string[];
    contest: number;
    consensus: string;
}

interface Timestamp {
    nanos: number;
    seconds: number;
}

interface Acvr {
    audit_cvr: {
        ballot_type: string;
        batch_id: string;
        contest_info: ContestInfo[];
        county_id: number;
        id: number;
        imprinted_id: string;
        record_id: string;
        record_type: string;
        scanner_id: string;
        timestamp: Timestamp;
    };
    cvr_id: number;
}

const formatContestInfo = (mark: any, contestId: any): ContestInfo => {
    const markedChoices: any = _.pickBy(mark.choices);
    const choices = _.map(markedChoices, (_, name: any) => name);

    const consensus = mark.noConsensus ? 'NO' : 'YES';

    return { choices, consensus, contest: contestId };
};

const formatDate = (d: Date): Timestamp => {
    // Date as ms since epoch.
    const t = d.getTime();

    // Truncate nanos.
    const seconds = Math.floor(t / 1000);

    // Take our ms since epoch, convert it to ns since epoch.
    // Then, remove the ns accounted for in `seconds`.
    const nanos = (t * 1000 * 1000) - (seconds * 1000 * 1000 * 1000);

    return { nanos, seconds };
};


export const format = (marks: any, cvr: any): Acvr => ({
    audit_cvr: {
        ballot_type: cvr.ballotType,
        batch_id: cvr.batchId,
        contest_info: _.map(marks, formatContestInfo),
        county_id: cvr.countyId,
        id: cvr.id,
        imprinted_id: cvr.imprintedId,
        record_id: cvr.recordId,
        record_type: cvr.recordType,
        scanner_id: cvr.scannerId,
        timestamp: formatDate(new Date()),
    },
    cvr_id: cvr.id,
});
