declare namespace JSON {
    interface AuditBoardMember {
        first_name: string;
        last_name: string;
        political_party: string;
    }

    interface AuditBoard {
        members: JSON.AuditBoardMember[];
        sign_in_time: Date;
    }

    interface RiskLimit {
        risk_limit: number;
    }

    interface Elector {
        first_name: string;
        last_name: string;
    }

    interface ContestInfo {
        choices: string[];
        comment: string;
        contest: number;
        consensus: string;
    }

    interface ACVR {
        audit_cvr: JSON.CVR;
        cvr_id: number;
    }

    interface Round {
        actual_count: number;
        disagreements: number;
        discrepancies: number;
        expected_count: number;
        number: number;
        signatories: JSON.Elector[];
        start_audit_prefix_length: number;
        start_index: number;
        start_time: Date;
    }

    interface CountyDashboard {
        asm_state: string;
        audit_board: any;
        audit_info: any;
        audit_time: string;
        audited_ballot_count: number;
        audited_prefix_length: number;
        ballot_manifest_count: number;
        ballot_manifest_file: any;
        ballot_under_audit_id: number;
        ballots_remaining_in_round: number;
        current_round: JSON.Round;
        cvr_export_count: number;
        cvr_export_file: any;
        cvr_import_status: CVRImportStatus;
        contests: number[];
        contests_under_audit: number[];
        disagreement_count: number;
        discrepancy_count: number;
        estimated_ballots_to_audit: number;
        general_information: string;
        id: number;
        risk_limit: number;
        rounds: JSON.Round[];
        status: CountyDashboardStatus;
    }

    interface CVRImportStatus {
        error_message?: string;
        import_state: County.CVRImportState;
        timestamp: string;
    }

    interface ContestChoice {
        name: string;
        description: string;
    }

    interface CVR {
        audited?: boolean;
        ballot_type: string;
        batch_id: number;
        contest_info: JSON.ContestInfo[];
        county_id: number;
        cvr_number: number;
        db_id?: number;
        id: number;
        imprinted_id: string;
        record_id: number;
        record_type: RecordType;
        scanner_id: number;
        storage_location?: string;
        timestamp: Date;
    }

    interface UploadedFile {
        approximate_record_count: number;
        county_id: number;
        file_id: number;
        filename: string;
        hash: string;
        hash_status: string;
        status: string;
        size: number;
        timestamp: string;
    }

    type UploadFileOk = JSON.UploadedFile;

    type UploadBallotManifestOk = JSON.UploadFileOk;

    type UploadCVRExportOk = JSON.UploadFileOk;

    interface FetchCountyASMStateOk {
        current_state: County.ASMState;
        enabled_ui_events: string[];
    }

    interface FetchDOSASMStateOk {
        current_state: DOS.ASMState;
        enabled_ui_events: string[];
    }

    interface Contest {
        choices: JSON.ContestChoice[];
        county_id: number;
        description: string;
        id: number;
        name: string;
        votes_allowed: number;
    }

    interface ContestForAudit {
        audit: AuditType;
        contest: number;
        reason: string;
    }
}
