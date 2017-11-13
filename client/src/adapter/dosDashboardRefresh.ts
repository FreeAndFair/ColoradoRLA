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

function parseDisagreementCount(data: any): Option<number> {
    if (_.isEmpty(data)) {
        return null;
    }

    return data.AUDITED_CONTEST;
}

function parseDiscrepancyCounts(data: any): any {
    if (_.isEmpty(data)) {
        return null;
    }

    const audited = data.AUDITED_CONTEST;
    const unaudited = data.UNAUDITED_CONTEST;

    return { audited, unaudited };
}

function parseFile(file: any): any {
    if (!file) { return null; }

    return {
        countyId: file.county_id,
        hash: file.hash,
        hashStatus: file.hash_status,
        id: file.file_id,
        name: file.filename,
        size: file.size,
        status: file.status,
        timestamp: new Date(file.timestamp),
    };
}

function parseCountyStatus(countyStatus: any) {
    const result: any = {};

    _.forEach(countyStatus, c => {
        result[c.id] = {
            asmState: c.asm_state,
            auditBoard: parseAuditBoard(c.audit_board),
            auditBoardASMState: c.audit_board_asm_state,
            auditedBallotCount: c.audited_ballot_count,
            ballotManifest: parseFile(c.ballot_manifest_file),
            ballotsRemainingInRound: c.ballots_remaining_in_round,
            currentRound: parseRound(c.current_round),
            cvrExport: parseFile(c.cvr_export_file),
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

function parsePublicMeetingDate(data: any): Option<Date> {
    if (!_.has(data, 'audit_info.public_meeting_date')) {
        return null;
    }

    const date = data.audit_info.public_meeting_date;

    return new Date(date);
}

function parseRiskLimit(data: any): Option<number> {
    const info = data.audit_info;

    if (!info) {
        return null;
    }

    return info.risk_limit;
}

function parseASMState(data: any): any {
    return data.asm_state;
}

function parseMember(member: any): any {
    return {
        firstName: member.first_name,
        lastName: member.last_name,
        party: member.political_party,
    };
}

function parseAuditBoard(data: any): any {
    if (!data) {
        return null;
    }

    const members = data.members.map(parseMember);
    const signIn = new Date(data.sign_in_time);

    return {
        members,
        signIn,
    };
}

export function parse(data: any) {
    return {
        asm: parseASMState(data),
        auditReasons: data.audit_reasons,
        auditTypes: data.audit_types,
        auditedContests: parseAuditedContests(data.audited_contests),
        countyStatus: parseCountyStatus(data.county_status),
        discrepancyCounts: data.discrepancy_count,
        election: parseElection(data),
        estimatedBallotsToAudit: data.estimated_ballots_to_audit,
        handCountContests: data.hand_count_contests,
        publicMeetingDate: parsePublicMeetingDate(data),
        riskLimit: parseRiskLimit(data),
        seed: _.get(data, 'audit_info.seed'),
    };
}
