import * as React from 'react';
import { connect } from 'react-redux';

import SoSRootPage from './SoSRootPage';


class SoSRootContainer extends React.Component<any, any> {
    public render() {
        return <SoSRootPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(SoSRootContainer);
