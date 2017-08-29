import * as _ from 'lodash';


interface Elector {
    first_name: string;
    last_name: string;
    political_party: string;
}

type Status = 'NO_DATA' | 'CVRS_UPLOADED_SUCCESSFULLY' | 'ERROR_IN_UPLOADED_DATA';

interface CountyDashboard {
    asm_state: string;
    audit_time: string;
    audit_board: any;
    audited_ballot_count: number;
    audited_prefix_length: number;
    ballot_manifest_filename: string;
    ballot_manifest_hash: string;
    ballot_under_audit_id: number;
    ballots_remaining_in_round: number;
    ballots_to_audit: number[];
    cvr_export_filename: string;
    cvr_export_hash: string;
    contests: number[];
    contests_under_audit: number[];
    disagreement_count: number;
    discrepancy_count: number;
    estimated_ballots_to_audit: number;
    general_information: string;
    id: number;

    status: Status;
}

const parseBoardMember = (e: Elector): any => ({
    firstName: e.first_name,
    lastName: e.last_name,
    party: e.political_party,
});

const parseAuditBoard = (board: any) => {
    if (!board) {
        return [];
    }

    return board.members.map(parseBoardMember);
};

const parseTimestamp = (ts: string): Date => new Date(ts);

const pivot = (a: any) => {
    const o: any = {};

    a.forEach((v: any) => {
        o[v.id] = v;
    });

    return o;
};

export const parseContests = (contestIds: any, state: any): any => {
    if (!state.county.contestDefs) {
        return [];
    }

    if (_.isEmpty(state.county.contestDefs)) {
        return [];
    }

    const { contestDefs } = state.county;

    return _.map(contestIds, (id: any) => contestDefs[id]);
};

const parseContestsUnderAudit = (contestIds: any, state: any): any => {
    if (!state.county.contestDefs) {
        return [];
    }

    if (_.isEmpty(state.county.contestDefs)) {
        return [];
    }

    const { contestDefs } = state.county;

    return _.map(contestIds, (reason: any, id: any) => {
        const def = state.county.contestDefs[id];
        return { ...def, reason };
    });
};


export const parse = (data: CountyDashboard, state: any): any => {
    const findContest = (id: any) => state.county.contestDefs[id];

    return {
        asm_state: data.asm_state,
        auditBoard: parseAuditBoard(data.audit_board),
        auditTime: data.audit_time ? parseTimestamp(data.audit_time) : null,
        auditedBallotCount: data.audited_ballot_count,
        auditedPrefixLength: data.audited_prefix_length,
        ballotManifestFilename: data.ballot_manifest_filename,
        ballotManifestHash: data.ballot_manifest_hash,
        ballotUnderAuditId: data.ballot_under_audit_id,
        ballotsRemainingInRound: data.ballots_remaining_in_round,
        ballotsToAudit: data.ballots_to_audit,
        contests: parseContests(data.contests, state),
        contestsUnderAudit: parseContestsUnderAudit(data.contests_under_audit, state),
        cvrExportFilename: data.cvr_export_filename,
        cvrExportHash: data.cvr_export_hash,
        disagreementCount: data.disagreement_count,
        discrepancyCount: data.discrepancy_count,
        estimatedBallotsToAudit: data.estimated_ballots_to_audit,
        generalInformation: data.general_information,
        id: data.id,
        status: data.status,
    };
};
