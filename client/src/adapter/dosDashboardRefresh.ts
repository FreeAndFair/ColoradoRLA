import * as _ from 'lodash';


const parseCountyStatus = (countyStatus: any) => {
    const result: any = {};

    _.forEach(countyStatus, (status, strId) => {
        const id = parseInt(strId, 10);

        result[id] = status;
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
    auditedContests: parseAuditedContests(data.audited_contests),
    auditStage: data.audit_stage,
    countyStatus: parseCountyStatus(data.county_status),
    handCountContests: data.hand_count_contests,
    riskLimit: data.risk_limit,
    seed: data.random_seed,
});
