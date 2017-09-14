import * as React from 'react';
import { connect } from 'react-redux';

import CVRExportForm from './Form';
import Uploading from './Uploading';

import uploadCvrExport from 'corla/action/county/uploadCvrExport';

import cvrExportUploadedSelector from 'corla/selector/county/cvrExportUploaded';


const UploadedCVRExport = ({ enableReupload, filename, hash }: any) => (
    <div className='pt-card'>
        <div>CVR Export <strong>uploaded</strong>.</div>
        <div>File name: "{ filename }"</div>
        <div>SHA-256 hash: { hash }</div>
        <button className='pt-button' onClick={ enableReupload }>
            Re-upload
        </button>
    </div>
);

class CVRExportFormContainer extends React.Component<any, any> {
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
                <UploadedCVRExport enableReupload={ this.enableReupload }
                                   filename={ county.ballotManifestFilename }
                                   hash={ county.ballotManifestHash } />
            );
        }

        return (
            <CVRExportForm disableReupload={ this.disableReupload }
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

        uploadCvrExport(county.id, file, hash);

        this.disableReupload();
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    const uploadingFile = !!county.uploadingCvrExport;

    return {
        county,
        fileUploaded: cvrExportUploadedSelector(state),
        uploadingFile,
    };
};


export default connect(mapStateToProps)(CVRExportFormContainer);
