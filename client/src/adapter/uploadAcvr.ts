import * as _ from 'lodash';


const formatContestInfo = (mark: County.ACVRContest, contestId: number): JSON.ContestInfo => {
    const markedChoices: County.ACVRChoices = _.pickBy(mark.choices);
    const choices = _.map(markedChoices, (_, name) => name);

    const comment = mark.comments;
    const consensus = mark.noConsensus ? 'NO' : 'YES';

    return { choices, comment, consensus, contest: contestId };
};


export const format = (marks: County.ACVR, cvr: CVR): JSON.ACVR => ({
    audit_cvr: {
        ballot_type: cvr.ballotType,
        batch_id: cvr.batchId,
        contest_info: _.map(marks, formatContestInfo),
        county_id: cvr.countyId,
        cvr_number: cvr.cvrNumber,
        id: cvr.id,
        imprinted_id: cvr.imprintedId,
        record_id: cvr.recordId,
        record_type: cvr.recordType,
        scanner_id: cvr.scannerId,
        timestamp: new Date(),
    },
    cvr_id: cvr.id,
});
