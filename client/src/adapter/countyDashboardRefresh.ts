import * as _ from 'lodash';


interface Elector {
    first_name: string;
    last_name: string;
    political_party: string;
}

interface Timestamp {
    nanos: number;
    seconds: number;
}

type Status = 'NO_DATA' | 'CVRS_UPLOADED_SUCCESSFULLY' | 'ERROR_IN_UPLOADED_DATA';

interface CountyDashboard {
    audit_board_members: Elector[];
    audit_time: Timestamp;
    audited_ballot_count: number;
    ballot_manifest_digest: string;
    ballot_under_audit_id: number;
    ballots_to_audit: number[];
    cvr_export_digest: string;
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

const parseTimestamp = (ts: Timestamp): Date => {
    const t = (ts.seconds * 1000) + (ts.nanos / 1000 / 1000);

    const d = new Date();
    d.setTime(t);

    return d;
};

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
        auditBoardMembers: data.audit_board_members.map(parseBoardMember),
        auditTime: data.audit_time ? parseTimestamp(data.audit_time) : null,
        auditedBallotCount: data.audited_ballot_count,
        ballotManifestDigest: data.ballot_manifest_digest,
        ballotUnderAuditId: data.ballot_under_audit_id,
        ballotsToAudit: data.ballots_to_audit,
        contests: parseContests(data.contests, state),
        contestsUnderAudit: parseContestsUnderAudit(data.contests_under_audit, state),
        cvrExportDigest: data.cvr_export_digest,
        disagreementCount: data.disagreement_count,
        discrepancyCount: data.discrepancy_count,
        estimatedBallotsToAudit: data.estimated_ballots_to_audit,
        generalInformation: data.general_information,
        id: data.id,
        status: data.status,
    };
};
