import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import BallotManifestUploader from './BallotManifestUploader';

import uploadBallotManifest from '../../../action/uploadBallotManifest';


const UploadedBallotManifest = ({ hash }: any) => (
    <div className='pt-card'>
        <div>Ballot manifest <strong>uploaded</strong>.</div>
        <div>SHA-256 hash: { hash }</div>
    </div>
);


class BallotManifestUploaderContainer extends React.Component<any, any> {
    public render() {
        const { auditStarted, county, uploadBallotManifest } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.ballotManifestForm;

            uploadBallotManifest(county.id, file, hash);
        };

        if (auditStarted) {
            return <UploadedBallotManifest hash={ county.ballotManifestHash } />;
        }

        return <BallotManifestUploader upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({
    auditStarted: !!county.ballotUnderAuditId,
    county,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    uploadBallotManifest,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(BallotManifestUploaderContainer);
