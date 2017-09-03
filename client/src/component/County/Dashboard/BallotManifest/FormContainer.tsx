import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestForm from './Form';

import uploadBallotManifest from 'corla/action/uploadBallotManifest';

import ballotManifestUploadedSelector from 'corla/selector/county/ballotManifestUploaded';


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


class BallotManifestFormContainer extends React.Component<any, any> {
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

        return (
            <BallotManifestForm
                disableReupload={ this.disableReupload }
                fileUploaded={ fileUploaded }
                upload={ upload }
                forms={ forms } />
        );
    }

    private disableReupload = () => this.setState({ reupload: false });

    private enableReupload = () => this.setState({ reupload: true });
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    return {
        auditStarted: !!county.ballotUnderAuditId,
        county,
        fileUploaded: ballotManifestUploadedSelector(state),
    };
};

export default connect(mapStateToProps)(BallotManifestFormContainer);
