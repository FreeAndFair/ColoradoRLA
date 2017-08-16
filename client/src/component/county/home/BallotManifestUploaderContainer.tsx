import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import BallotManifestUploader from './BallotManifestUploader';

import uploadBallotManifest from '../../../action/uploadBallotManifest';


class BallotManifestUploaderContainer extends React.Component<any, any> {
    public render() {
        const { county, uploadBallotManifest } = this.props;
        const forms: any = {};

        const upload = () => {
            const { file, hash } = forms.ballotManifestForm;

            uploadBallotManifest(county.id, file, hash);
        };

        return <BallotManifestUploader upload={ upload } forms={ forms } />;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    uploadBallotManifest,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(BallotManifestUploaderContainer);
