import * as React from 'react';
import { connect } from 'react-redux';

import SoSHomePage from './SoSHomePage';


class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        return <SoSHomePage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => ({
    counties: state.sos.counties,
    sos: state.sos,
});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(SoSHomeContainer);
