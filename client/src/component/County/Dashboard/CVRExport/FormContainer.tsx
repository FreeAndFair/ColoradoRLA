import * as React from 'react';
import { connect } from 'react-redux';

import CVRExportForm from './Form';
import Uploading from './Uploading';

import uploadCvrExport from 'corla/action/county/uploadCvrExport';

import cvrExportUploadedSelector from 'corla/selector/county/cvrExportUploaded';


interface UploadedProps {
    enableReupload: OnClick;
    file: UploadedFile;
}

const UploadedCVRExport = (props: UploadedProps) => {
    const { enableReupload, file } = props;
    return (
        <div className='pt-card'>
            <div>CVR Export <strong>uploaded</strong>.</div>
            <div>File name: "{ file.name }"</div>
            <div>SHA-256 hash: { file.hash }</div>
            <button className='pt-button' onClick={ enableReupload }>
                Re-upload
            </button>
        </div>
    );
};

interface ContainerProps {
    county: County.AppState;
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

class CVRExportFormContainer extends React.Component<ContainerProps, ContainerState> {
    public state: ContainerState = {
        form: {
            file: undefined,
            hash: '',
        },
        reupload: false,
    };

    public render() {
        const { county, fileUploaded, uploadingFile } = this.props;

        if (uploadingFile) {
            return <Uploading county={ county } />;
        }

        if (fileUploaded && !this.state.reupload && county.cvrExport) {
            return (
                <UploadedCVRExport enableReupload={ this.enableReupload }
                                   file={ county.cvrExport } />
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

        uploadCvrExport(county!.id!, file!, hash);

        this.disableReupload();
    }
}

const select = (state: AppState) => {
    const { county } = state;

    const uploadingFile = !!county!.uploadingCvrExport;

    return {
        county,
        fileUploaded: cvrExportUploadedSelector(state),
        uploadingFile,
    };
};


export default connect(select)(CVRExportFormContainer);
