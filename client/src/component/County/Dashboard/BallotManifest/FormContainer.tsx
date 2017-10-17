import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestForm from './Form';
import Uploading from './Uploading';

import uploadBallotManifest from 'corla/action/county/uploadBallotManifest';

import ballotManifestUploadedSelector from 'corla/selector/county/ballotManifestUploaded';


interface UploadedProps {
    enableReupload: OnClick;
    file: UploadedFile;
}

const UploadedBallotManifest = (props: UploadedProps) => {
    const { enableReupload, file } = props;

    return (
        <div className='pt-card'>
            <div>Ballot Manifest <strong>uploaded</strong>.</div>
            <div>File name: "{ file.name }"</div>
            <div>SHA-256 hash: { file.hash }</div>
            <button className='pt-button' onClick={ enableReupload }>
                Re-upload
            </button>
        </div>
    );
};

interface ContainerProps {
    county: CountyState;
    fileUploaded: boolean;
    uploadingFile: boolean;
}

interface ContainerState {
    form: {
        file?: File;
        hash: string;
    };
    reupload: boolean;
}

class BallotManifestFormContainer extends React.Component<ContainerProps, ContainerState> {
    public state: ContainerState = {
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

        if (fileUploaded && !this.state.reupload && county.ballotManifest) {
            return (
                <UploadedBallotManifest enableReupload={ this.enableReupload }
                                        file={ county.ballotManifest } />
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

    private onFileChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };

        s.form.file = e.target.files[0];

        this.setState(s);
    }

    private onHashChange = (hash: string) => {
        const s = { ...this.state };

        s.form.hash = hash;

        this.setState(s);
    }

    private upload = () => {
        const { county } = this.props;
        const { file, hash } = this.state.form;

        uploadBallotManifest(county.id, file, hash);

        this.disableReupload();
    }
}

const select = (state: AppState) => {
    const { county } = state;

    const uploadingFile = !!county.uploadingBallotManifest;

    return {
        county,
        fileUploaded: ballotManifestUploadedSelector(state),
        uploadingFile,
    };
};


export default connect(select)(BallotManifestFormContainer);
