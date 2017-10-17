import * as _ from 'lodash';


function parseBoardMember(e: AuditBoardMemberJson): AuditBoardMember {
    return {
        firstName: e.first_name,
        lastName: e.last_name,
        party: e.political_party,
    };
}

function parseAuditBoard(board: any): AuditBoard {
    if (!board) {
        return [];
    }

    return board.members.map(parseBoardMember);
}

function parseTimestamp(ts: string): Date {
    return new Date(ts);
}

function pivot(a: any): any {
    const o: any = {};

    a.forEach((v: any) => {
        o[v.id] = v;
    });

    return o;
}

export function parseContests(contestIds: number[], state: AppState): any[] {
    if (!state.county.contestDefs) {
        return [];
    }

    if (_.isEmpty(state.county.contestDefs)) {
        return [];
    }

    const { contestDefs } = state.county;

    return _.map(contestIds, (id: any) => contestDefs[id]);
}

function parseContestsUnderAudit(contestIds: number[], state: AppState): any[] {
    if (!state.county.contestDefs) {
        return [];
    }

    if (_.isEmpty(state.county.contestDefs)) {
        return [];
    }

    const { contestDefs } = state.county;

    return _.map(contestIds, (reason, id) => {
        const def = state.county.contestDefs[id];
        return { ...def, reason };
    });
}

function parseSignatories(s?: ElectorJson[]): Elector[] {
    if (!s) {
        return [];
    }

    return s.map(e => {
        return {
            firstName: e.first_name,
            lastName: e.last_name,
        };
    });
}

function parseRound(data: RoundJson): Round {
    return {
        actualCount: data.actual_count,
        disagreements: data.disagreements,
        discrepancies: data.discrepancies,
        expectedCount: data.expected_count,
        number: data.number,
        signatories: parseSignatories(data.signatories),
        startAuditPrefixLength: data.start_audit_prefix_length,
        startIndex: data.start_index,
        startTime: data.start_time,
    };
}

function parseRounds(rounds?: RoundJson[]): Round[] {
    if (!rounds) {
        return [];
    }

    return rounds.map(parseRound);
}

function parseElection(data: CountyDashboardJson): Election {
    return {
        date: new Date(data.audit_info.election_date),
        type: data.audit_info.election_type,
    };
}

function parseRiskLimit(data: CountyDashboardJson): number {
    return _.get(data, 'audit_info.risk_limit');
}

function parseDisCount(data: any): number {
    return _.sum(_.values(data));
}

function parseFile(file: UploadedFileJson): UploadedFile {
    if (!file) { return null; }

    return {
        approximateRecordCount: file.approximate_record_count,
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

export function parse(data: CountyDashboardJson, state: AppState) {
    const findContest = (id: number) => state.county.contestDefs[id];

    return {
        asm_state: data.asm_state,
        auditBoard: parseAuditBoard(data.audit_board),
        auditTime: data.audit_time ? parseTimestamp(data.audit_time) : null,
        auditedBallotCount: data.audited_ballot_count,
        auditedPrefixLength: data.audited_prefix_length,
        ballotManifest: parseFile(data.ballot_manifest_file),
        ballotManifestCount: data.ballot_manifest_count,
        ballotUnderAuditId: data.ballot_under_audit_id,
        ballotsRemainingInRound: data.ballots_remaining_in_round,
        contests: parseContests(data.contests, state),
        contestsUnderAudit: parseContestsUnderAudit(data.contests_under_audit, state),
        currentRound: data.current_round ? parseRound(data.current_round) : null,
        cvrExport: parseFile(data.cvr_export_file),
        cvrExportCount: data.cvr_export_count,
        cvrImportStatus: data.cvr_import_status,
        disagreementCount: parseDisCount(data.disagreement_count),
        discrepancyCount: parseDisCount(data.discrepancy_count),
        election: parseElection(data),
        estimatedBallotsToAudit: data.estimated_ballots_to_audit,
        generalInformation: data.general_information,
        id: data.id,
        riskLimit: parseRiskLimit(data),
        rounds: parseRounds(data.rounds),
        status: data.status,
    };
}
