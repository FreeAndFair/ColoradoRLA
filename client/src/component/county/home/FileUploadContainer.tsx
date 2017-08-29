import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestUploadFormContainer from './BallotManifestUploadFormContainer';
import CVRExportUploadFormContainer from './CVRExportUploadFormContainer';


const FileUploadForms = () => {
    return (
        <div>
            <BallotManifestUploadFormContainer />
            <CVRExportUploadFormContainer />
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

class FileUploadContainer extends React.Component<any, any> {
    public render() {
        const { missedDeadline } = this.props;

        if (missedDeadline) {
            return <MissedDeadline />;
        }

        return <FileUploadForms />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;
    const { asm } = county;

    const auditInProgress = asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS';
    const uploadedBothFiles = county.ballotManifestHash && county.cvrExportHash;
    const missedDeadline = auditInProgress && !uploadedBothFiles;

    return { missedDeadline };
};


export default connect(mapStateToProps)(FileUploadContainer);
