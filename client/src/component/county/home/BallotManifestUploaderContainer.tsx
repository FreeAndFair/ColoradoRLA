import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestUploader from './BallotManifestUploader';

import uploadBallotManifest from '../../../action/uploadBallotManifest';


const UploadedBallotManifest = ({ filename, hash }: any) => (
    <div className='pt-card'>
        <div>Ballot manifest <strong>uploaded</strong>.</div>
        <div>File name: "{ filename }"</div>
        <div>SHA-256 hash: { hash }</div>
    </div>
);


class BallotManifestUploaderContainer extends React.Component<any, any> {
    public render() {
        const { auditStarted, county, fileUploaded } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.ballotManifestForm;

            uploadBallotManifest(county.id, file, hash);
        };

        if (fileUploaded) {
            return (
                <UploadedBallotManifest
                    filename={ county.ballotManifestFilename }
                    hash={ county.ballotManifestHash } />
            );
        }

        return <BallotManifestUploader upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({
    auditStarted: !!county.ballotUnderAuditId,
    county,
    fileUploaded: !!county.ballotManifestHash,
});

export default connect(mapStateToProps)(BallotManifestUploaderContainer);
