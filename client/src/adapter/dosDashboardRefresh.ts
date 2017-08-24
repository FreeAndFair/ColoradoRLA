import * as _ from 'lodash';


const parseCountyStatus = (countyStatus: any) => {
    const result: any = {};

    _.forEach(countyStatus, c => {
        result[c.id] = {
            auditedBallotCount: c.audited_ballot_count,
            ballotManifestHash: c.ballot_manifest_hash,
            cvrExportHash: c.cvr_export_hash,
            cvrTimestamp: c.cvr_export_timestamp,
            disagreementCount: c.disagreement_count,
            discrepancyCount: c.discrepancy_count,
            estimatedBallotsToAudit: c.estimated_ballots_to_audit,
            id: c.id,
            manifestTimestamp: c.ballot_manifest_timestamp,
            status: c.status,
        };
    });

    return result;
};

const parseAuditedContests = (data: any) => {
    const result: any = {};

    _.forEach(data, (reason: any, idStr: any) => {
        const id = parseInt(idStr, 10);
        result[id] = { id, reason };
    });

    return result;
};


export const parse = (data: any) => ({
    auditStage: data.audit_stage,
    auditedContests: parseAuditedContests(data.audited_contests),
    countyStatus: parseCountyStatus(data.county_status),
    handCountContests: data.hand_count_contests,
    riskLimit: data.risk_limit,
    seed: data.random_seed,
});
