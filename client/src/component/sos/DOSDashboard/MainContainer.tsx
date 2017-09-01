import * as React from 'react';
import { connect } from 'react-redux';

import Main from './Main';

import auditStarted from '../../../selector/dos/auditStarted';
import canRenderReport from '../../../selector/dos/canRenderReport';


class MainContainer extends React.Component<any, any> {
    public render() {
        return <Main { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        auditDefined: auditStarted(state),
        canRenderReport: canRenderReport(state),
        sos,
    };
};


export default connect(mapStateToProps)(MainContainer);
