import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestFormContainer from './BallotManifest/FormContainer';
import CVRExportFormContainer from './CVRExport/FormContainer';


interface MatchStatusProps {
    ballotManifestCount: number;
    cvrExportCount: number;
    uploadedBothFiles: boolean;
}

const MatchStatus = (props: MatchStatusProps) => {
    const { ballotManifestCount, cvrExportCount, uploadedBothFiles } = props;

    if (!uploadedBothFiles) {
        return <div />;
    }

    if (ballotManifestCount === cvrExportCount) {
        return (
            <div className='pt-card' >
                <span className='pt-icon pt-intent-success pt-icon-tick-circle' />
                <span> </span>
                CVR Export and Ballot Manifest record counts <strong>match.</strong>
            </div>
        );
    } else {
        return (
            <div className='pt-card' >
                <span className='pt-icon pt-intent-danger pt-icon-error' />
                <span> </span>
                CVR Export and Ballot Manifest record counts <strong>do not match.</strong>
                <div className='pt-card' >
                    <div>Ballot Manifest count: { ballotManifestCount }</div>
                    <div>CVR Export count: { cvrExportCount }</div>
                </div>
            </div>
        );
    }
};

interface FileUploadFormsProps {
    county: CountyState;
    uploadedBothFiles: boolean;
}

const FileUploadForms = (props: FileUploadFormsProps) => {
    const { county, uploadedBothFiles } = props;
    const { ballotManifestCount, cvrExportCount } = county;

    return (
        <div>
            <MatchStatus ballotManifestCount={ ballotManifestCount }
                         cvrExportCount={ cvrExportCount }
                         uploadedBothFiles={ uploadedBothFiles } />
            <BallotManifestFormContainer />
            <CVRExportFormContainer />
        </div>
    );
};

const MissedDeadline = () => {
    return (
        <div className='pt-card'>
            The Risk-Limiting Audit has already begun.
            Please contact the Department of State for assistance.
        </div>
    );
};

interface FileUploadContainerProps {
    county: CountyState;
    missedDeadline: boolean;
    uploadedBothFiles: boolean;
}

class FileUploadContainer extends React.Component<FileUploadContainerProps> {
    public render() {
        const { county, missedDeadline, uploadedBothFiles } = this.props;

        if (missedDeadline) {
            return <MissedDeadline />;
        }

        return (
            <FileUploadForms county={ county }
                             uploadedBothFiles={ uploadedBothFiles } />
        );
    }
}

const select = (state: AppState) => {
    const { county } = state;
    const { asm } = county;

    const auditInProgress = asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS';
    const uploadedBothFiles = !!(county.ballotManifestHash && county.cvrExportHash);
    const missedDeadline = auditInProgress && !uploadedBothFiles;

    return { county, missedDeadline, uploadedBothFiles };
};


export default connect(select)(FileUploadContainer);
