import * as React from 'react';
import { connect } from 'react-redux';

import CVRExportForm from './Form';

import uploadCvrExport from 'corla/action/uploadCvrExport';

import cvrExportUploadedSelector from 'corla/selector/county/cvrExportUploaded';


const UploadedCvrExport = ({ enableReupload, filename, hash }: any) => (
    <div className='pt-card'>
        <div>CVR export <strong>uploaded</strong>.</div>
        <div>File name: "{ filename }"</div>
        <div>SHA-256 hash: { hash }</div>
        <button className='pt-button' onClick={ enableReupload }>
            Re-upload
        </button>
    </div>
);

class CVRExportFormContainer extends React.Component<any, any> {
    public state = { reupload: false };

    public render() {
        const { auditStarted, county, fileUploaded } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.cvrExportForm;

            uploadCvrExport(county.id, file, hash);
            this.disableReupload();
        };

        if (fileUploaded && !this.state.reupload) {
            return (
                <UploadedCvrExport
                    enableReupload={ this.enableReupload }
                    filename={ county.cvrExportFilename }
                    hash={ county.cvrExportHash } />
            );
        }

        return (
            <CVRExportForm
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
        fileUploaded: cvrExportUploadedSelector(state),
    };
};


export default connect(mapStateToProps)(CVRExportFormContainer);
