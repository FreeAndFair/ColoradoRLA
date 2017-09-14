import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestFormContainer from './BallotManifest/FormContainer';
import CVRExportFormContainer from './CVRExport/FormContainer';


const MatchStatus = (props: any) => {
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
            </div>
        );
    }
};

const FileUploadForms = (props: any) => {
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

class FileUploadContainer extends React.Component<any, any> {
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

const mapStateToProps = (state: any) => {
    const { county } = state;
    const { asm } = county;

    const auditInProgress = asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS';
    const uploadedBothFiles = county.ballotManifestHash && county.cvrExportHash;
    const missedDeadline = auditInProgress && !uploadedBothFiles;

    return { county, missedDeadline, uploadedBothFiles };
};


export default connect(mapStateToProps)(FileUploadContainer);
