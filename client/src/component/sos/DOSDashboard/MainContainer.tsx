import * as React from 'react';
import { connect } from 'react-redux';

import Main from './Main';

import auditStarted from '../../../selector/dos/auditStarted';


class MainContainer extends React.Component<any, any> {
    public render() {
        return <Main { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        showAuditParams: auditStarted(state),
        sos,
    };
};


export default connect(mapStateToProps)(MainContainer);
