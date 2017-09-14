import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestForm from './Form';

import uploadBallotManifest from 'corla/action/county/uploadBallotManifest';

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
    public state: any = {
        form: {
            file: null,
            hash: '',
        },
        reupload: false,
    };

    public render() {
        const { auditStarted, county, fileUploaded } = this.props;

        if (fileUploaded && !this.state.reupload) {
            return (
                <UploadedBallotManifest enableReupload={ this.enableReupload }
                                        filename={ county.ballotManifestFilename }
                                        hash={ county.ballotManifestHash } />
            );
        }

        return (
            <BallotManifestForm disableReupload={ this.disableReupload }
                                fileUploaded={ fileUploaded }
                                form={ this.state.form }
                                onFileChange={ this.onFileChange }
                                onHashChange={ this.onHashChange }
                                upload={ this.upload } />
        );
    }

    private disableReupload = () => {
        this.setState({ reupload: false });
    }

    private enableReupload = () => {
        this.setState({ reupload: true });
    }

    private onFileChange = (e: any) => {
        const s = { ...this.state };

        s.form.file = e.target.files[0];

        this.setState(s);
    }

    private onHashChange = (hash: any) => {
        const s = { ...this.state };

        s.form.hash = hash;

        this.setState(s);
    }

    private upload = () => {
        const { county } = this.props;
        const { file, hash } = this.state.form;

        this.setUploading(true);

        uploadBallotManifest(county.id, file, hash);

        this.disableReupload();
    }
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
