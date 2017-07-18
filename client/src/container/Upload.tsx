import * as React from 'react';
import { connect } from 'react-redux';


class Upload extends React.Component<any, any> {
    public render() {
        return <div>Upload</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Upload);
