import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestForm from './Form';
import Uploading from './Uploading';

import uploadBallotManifest from 'corla/action/county/uploadBallotManifest';

import ballotManifestUploadedSelector from 'corla/selector/county/ballotManifestUploaded';


const UploadedBallotManifest = ({ filename, hash, enableReupload }: any) => (
    <div className='pt-card'>
        <div>Ballot Manifest <strong>uploaded</strong>.</div>
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
        const { county, fileUploaded, uploadingFile } = this.props;

        if (uploadingFile) {
            return <Uploading />;
        }

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

    private setFileTimestamp = (fileTimestamp: string) => {
        this.setState({
            ...this.state,
            fileTimestamp,
        });
    }

    private fileIsNew = () => {
        return this.state.fileTimestamp !== this.props.fileTimestamp;
    }

    private upload = () => {
        const { county } = this.props;
        const { file, hash } = this.state.form;

        uploadBallotManifest(county.id, file, hash);

        this.disableReupload();
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    const uploadingFile = !!county.uploadingBallotManifest;

    return {
        county,
        fileUploaded: ballotManifestUploadedSelector(state),
        uploadingFile,
    };
};


export default connect(mapStateToProps)(BallotManifestFormContainer);
