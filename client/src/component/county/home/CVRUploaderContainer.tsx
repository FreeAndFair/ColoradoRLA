import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import CVRUploader from './CVRUploader';

import uploadCvrExport from '../../../action/uploadCvrExport';


class CVRUploaderContainer extends React.Component<any, any> {
    public render() {
        const { county, uploadCvrExport } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.cvrExportForm;

            uploadCvrExport(county.id, file, hash);
        };

        return <CVRUploader upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    uploadCvrExport,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CVRUploaderContainer);
