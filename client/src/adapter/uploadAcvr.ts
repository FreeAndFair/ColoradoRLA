interface ContestInfo {
    choices: string[];
    contest: number;
}

interface Timestamp {
    nanos: number;
    seconds: number;
}

interface Acvr {
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
}

const formatContestInfo = (info: any): ContestInfo => {
    return {
        choices: info.choices,
        contest: info.contestId,
    };
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


export const format = (acvr: any): Acvr => {
    const result = {
        ballot_type: acvr.ballotType,
        batch_id: acvr.batchId,
        contest_info: acvr.contestInfo.map(formatContestInfo),
        county_id: acvr.countyID,
        id: acvr.id,
        imprinted_id: acvr.imprintedId,
        record_id: acvr.recordId,
        record_type: acvr.recordType,
        scanner_id: acvr.scannerId,
        timestamp: formatDate(acvr.timestamp),
    };


    return result;
};
