import * as React from 'react';
import { connect } from 'react-redux';

import CVRExportUploadForm from './CVRExportUploadForm';

import uploadCvrExport from '../../../action/uploadCvrExport';


const UploadedCvrExport = ({ filename, hash }: any) => (
    <div className='pt-card'>
        <div>CVR export <strong>uploaded</strong>.</div>
        <div>File name: "{ filename }"</div>
        <div>SHA-256 hash: { hash }</div>
    </div>
);

class CVRExportUploadFormContainer extends React.Component<any, any> {
    public render() {
        const { auditStarted, county, fileUploaded } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.cvrExportForm;

            uploadCvrExport(county.id, file, hash);
        };

        if (fileUploaded) {
            return (
                <UploadedCvrExport
                    filename={ county.cvrExportFilename }
                    hash={ county.cvrExportHash } />
            );
        }

        return <CVRExportUploadForm upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({
    auditStarted: !!county.ballotUnderAuditId,
    county,
    fileUploaded: !!county.cvrExportHash,
});


export default connect(mapStateToProps)(CVRExportUploadFormContainer);
