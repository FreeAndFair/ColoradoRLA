declare namespace County {
    interface AppState {
        acvrs: ACVRs;
        asm: ASMStates;
        auditBoard: AuditBoard;
        auditedBallotCount?: number;
        ballotManifest?: UploadedFile;
        ballotManifestCount?: number;
        ballotManifestHash?: string;
        ballotsRemainingInRound?: number;
        ballotUnderAuditId?: number;
        contests?: Contest[];
        contestDefs?: ContestDefs;
        contestsUnderAudit?: Contest[];
        currentBallot?: CurrentBallot;
        currentRound?: Option<Round>;
        cvrExport?: UploadedFile;
        cvrExportCount?: number;
        cvrExportHash?: string;
        cvrImportPending: CVRImportPending;
        cvrImportStatus: CVRImportStatus;
        cvrsToAudit?: JSON.CVR[];  // Sic
        disagreementCount?: number;
        discrepancyCount?: number;
        election?: Election;
        estimatedBallotsToAudit?: number;
        fileName?: string;  // TODO: remove
        hash?: string;  // TODO: remove
        id?: number;
        riskLimit?: number;
        rounds: Round[];
        type: 'County';
        uploadingBallotManifest?: boolean;
        uploadingCVRExport?: boolean;
    }

    interface CurrentBallot extends CVR {
        submitted: boolean;
    }

    interface CVRImportPending {
        alerted: boolean;
        started: Date;
    }

    interface CVRImportStatus {
        error?: string;
        state: CVRImportState;
        timestamp: Date;
    }

    type CVRImportState
        = 'NOT_ATTEMPTED'
        | 'IN_PROGRESS'
        | 'SUCCESSFUL'
        | 'FAILED';

    type CVRImportAlert = 'None' | 'Fail' | 'Ok';

    interface ASMStates {
        auditBoard: AuditBoardASMState;
        county: ASMState;
    }

    interface ACVRs {
        [cvrId: number]: ACVR;
    }

    interface ACVR {
        [contestId: number]: ACVRContest;
    }

    interface ACVRContest {
        choices: ACVRChoices;
        comments: string;
        noConsensus: boolean;
    }

    interface ACVRChoices {
        [contestChoice: string]: boolean;
    }

    interface ContestDefs {
        [id: number]: Contest;
    }

    type ASMState
        = 'COUNTY_INITIAL_STATE'
        | 'BALLOT_MANIFEST_OK'
        | 'CVRS_IMPORTING'
        | 'CVRS_OK'
        | 'BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING'
        | 'BALLOT_MANIFEST_AND_CVRS_OK'
        | 'COUNTY_AUDIT_UNDERWAY'
        | 'COUNTY_AUDIT_COMPLETE'
        | 'DEADLINE_MISSED';
}
