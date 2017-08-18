import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import CVRUploader from './CVRUploader';

import uploadCvrExport from '../../../action/uploadCvrExport';


const UploadedCvrExport = ({ hash }: any) => (
    <div className='pt-card'>
        <div>CVR export <strong>uploaded</strong>.</div>
        <div>SHA-256 hash: { hash }</div>
    </div>
);

class CVRUploaderContainer extends React.Component<any, any> {
    public render() {
        const { auditStarted, county, uploadCvrExport } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.cvrExportForm;

            uploadCvrExport(county.id, file, hash);
        };

        if (auditStarted) {
            return <UploadedCvrExport hash={ county.cvrExportHash } />;
        }

        return <CVRUploader upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({
    auditStarted: !!county.ballotUnderAuditId,
    county,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    uploadCvrExport,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CVRUploaderContainer);
