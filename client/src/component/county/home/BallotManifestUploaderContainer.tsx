import * as React from 'react';
import { connect } from 'react-redux';

import BallotManifestUploader from './BallotManifestUploader';


class BallotManifestUploaderContainer extends React.Component<any, any> {
    public render() {
        const onChange = (e: any) => {
            const input = e.target;
            const file = input.files[0];
            // Trigger async file upload action here.
        };

        // This will change in app state upon successful file upload.
        const fileName = 'No file uploaded.';

        return <BallotManifestUploader onChange={ onChange } fileName={ fileName } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(BallotManifestUploaderContainer);
