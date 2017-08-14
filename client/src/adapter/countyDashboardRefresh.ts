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
    ballot_manifest_digest: string;
    ballot_under_audit_id: number;
    ballots_to_audit: number[];
    cvr_export_digest: string;
    contests: number[];
    contests_under_audit: number[];
    estimated_ballots_to_audit: number;
    general_information: string;
    number_of_ballots_audited: number;
    number_of_disagreements: number;
    number_of_discrepancies: number;
    status: Status;
}

const parseBoardMember = (e: Elector): any => ({
    firstName: e.first_name,
    lastName: e.last_name,
    politicalParty: e.political_party,
});

const parseTimestamp = (ts: Timestamp): Date => {
    const t = (ts.seconds * 1000) + (ts.nanos / 1000 / 1000);

    const d = new Date();
    d.setTime(t);

    return d;
};


export const parse = (data: CountyDashboard, state: any): any => {
    const findContest = (id: any) => state.contests[id];

    return {
        auditBoardMembers: data.audit_board_members.map(parseBoardMember),
        auditTime: parseTimestamp(data.audit_time),
        ballotManifestDigest: data.ballot_manifest_digest,
        ballotUnderAuditId: data.ballot_under_audit_id,
        ballotsToAudit: data.ballots_to_audit,
        contests: data.contests.map(findContest),
        contestsUnderAudit: data.contests_under_audit.map(findContest),
        cvrExportDigest: data.cvr_export_digest,
        estimatedBallotsToAudit: data.estimated_ballots_to_audit,
        generalInformation: data.general_information,
        numberOfBallotsAudited: data.number_of_ballots_audited,
        numberOfDisagreements: data.number_of_disagreements,
        numberOfDiscrepancies: data.number_of_discrepancies,
        status: data.status,
    };
};
