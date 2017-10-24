import * as React from 'react';
import { connect } from 'react-redux';

import Main from './Main';

import auditStarted from 'corla/selector/dos/auditStarted';
import canRenderReport from 'corla/selector/dos/canRenderReport';


interface ContainerProps {
    auditDefined: boolean;
    canRenderReport: boolean;
    dosState: DOS.AppState;
}

class MainContainer extends React.Component<ContainerProps> {
    public render() {
        return <Main { ...this.props } />;
    }
}

function select(dosState: DOS.AppState) {
    return {
        auditDefined: auditStarted(dosState),
        canRenderReport: canRenderReport(dosState),
        dosState,
    };
}


export default connect(select)(MainContainer);
