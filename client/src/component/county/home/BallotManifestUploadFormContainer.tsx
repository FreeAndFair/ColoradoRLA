import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestUploadForm from './BallotManifestUploadForm';

import uploadBallotManifest from '../../../action/uploadBallotManifest';


const UploadedBallotManifest = ({ filename, hash, enableReupload }: any) => (
    <div className='pt-card'>
        <div>Ballot manifest <strong>uploaded</strong>.</div>
        <div>File name: "{ filename }"</div>
        <div>SHA-256 hash: { hash }</div>
        <button className='pt-button' onClick={ enableReupload }>
            Re-upload
        </button>
    </div>
);


class BallotManifestUploadFormContainer extends React.Component<any, any> {
    public state = { reupload: false };

    public render() {
        const { auditStarted, county, fileUploaded } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.ballotManifestForm;

            uploadBallotManifest(county.id, file, hash);
            this.disableReupload();
        };

        if (fileUploaded && !this.state.reupload) {
            return (
                <UploadedBallotManifest
                    enableReupload={ this.enableReupload }
                    filename={ county.ballotManifestFilename }
                    hash={ county.ballotManifestHash } />
            );
        }

        return <BallotManifestUploadForm upload={ upload } forms={ forms } />;
    }

    private disableReupload = () => this.setState({ reupload: false });

    private enableReupload = () => this.setState({ reupload: true });
}

const mapStateToProps = ({ county }: any) => ({
    auditStarted: !!county.ballotUnderAuditId,
    county,
    fileUploaded: !!county.ballotManifestHash,
});

export default connect(mapStateToProps)(BallotManifestUploadFormContainer);
