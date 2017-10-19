import * as React from 'react';
import { connect } from 'react-redux';

import Main from './Main';

import auditStarted from 'corla/selector/dos/auditStarted';
import canRenderReport from 'corla/selector/dos/canRenderReport';


interface ContainerProps {
    auditDefined: boolean;
    canRenderReport: boolean;
    sos: DOS.AppState;
}

class MainContainer extends React.Component<ContainerProps> {
    public render() {
        return <Main { ...this.props } />;
    }
}

function select(state: AppState) {
    const { sos } = state;

    if (!sos) { return {}; }

    return {
        auditDefined: auditStarted(state),
        canRenderReport: canRenderReport(state),
        sos,
    };
}


export default connect(select)(MainContainer);
