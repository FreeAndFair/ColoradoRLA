import * as _ from 'lodash';


function parseRound(data: any) {
    if (!data) {
        return {};
    }

    return {
        actualCount: data.actual_count,
        disagreements: data.disagreements,
        discrepancies: data.discrepancies,
        expectedCount: data.expected_count,
        number: data.number,
        startIndex: data.start_index,
        startTime: data.start_time,
    };
}

function parseRounds(rounds: any[]) {
    if (!rounds) {
        return [];
    }

    return rounds.map(parseRound);
}

function parseDisagreementCount(data: any): number {
    if (_.isEmpty(data)) {
        return null;
    }

    return _.sum(_.values(data));
}

function parseDiscrepancyCounts(data: any): any {
    if (_.isEmpty(data)) {
        return null;
    }

    const total = _.sum(_.values(data));
    const opportunistic = data.OPPORTUNISTIC_BENEFITS;
    const audited = total - opportunistic;

    return { audited, opportunistic };
}

function parseCountyStatus(countyStatus: any) {
    const result: any = {};

    _.forEach(countyStatus, c => {
        result[c.id] = {
            asmState: c.asm_state,
            auditBoardAsmState: c.audit_board_asm_state,
            auditedBallotCount: c.audited_ballot_count,
            ballotManifestHash: c.ballot_manifest_hash,
            ballotsRemainingInRound: c.ballots_remaining_in_round,
            currentRound: parseRound(c.current_round),
            cvrExportHash: c.cvr_export_hash,
            cvrTimestamp: c.cvr_export_timestamp,
            disagreementCount: parseDisagreementCount(c.disagreement_count),
            discrepancyCount: parseDiscrepancyCounts(c.discrepancy_count),
            estimatedBallotsToAudit: c.estimated_ballots_to_audit,
            id: c.id,
            manifestTimestamp: c.ballot_manifest_timestamp,
            rounds: parseRounds(c.rounds),
            status: c.status,
        };
    });

    return result;
}

function parseAuditedContests(data: any) {
    const result: any = {};

    _.forEach(data, (reason: any, idStr: any) => {
        const id = parseInt(idStr, 10);
        result[id] = { id, reason };
    });

    return result;
}

function parseElection(data: any): any {
    const info = data.audit_info;

    if (!info) {
        return null;
    }

    return {
        date: new Date(info.election_date),
        type: info.election_type,
    };
}

function parsePublicMeetingDate(data: any): Date {
    if (!_.has(data, 'audit_info.public_meeting_date')) {
        return null;
    }

    const date = data.audit_info.public_meeting_date;

    return new Date(date);
}

function parseRiskLimit(data: any): number {
    const info = data.audit_info;

    if (!info) {
        return null;
    }

    return info.risk_limit;
}


export const parse = (data: any) => ({
    asmState: data.asm_state,
    auditStage: data.audit_stage,
    auditedContests: parseAuditedContests(data.audited_contests),
    countyStatus: parseCountyStatus(data.county_status),
    election: parseElection(data),
    estimatedBallotsToAudit: data.estimated_ballots_to_audit,
    handCountContests: data.hand_count_contests,
    publicMeetingDate: parsePublicMeetingDate(data),
    riskLimit: parseRiskLimit(data),
    seed: data.random_seed,
});
