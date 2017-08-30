import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import auditStarted from '../../../selector/dos/auditStarted';
import canStartNextRound from '../../../selector/dos/canStartNextRound';


class RoundContainer extends React.Component<any, any> {
    public render() {
        if (this.props.canStartNextRound) {
            return <Control />;
        }

        const props = {
            auditStarted: this.props.auditStarted,
        };

        return <Status { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        auditStarted: auditStarted(state),
        canStartNextRound: canStartNextRound(state),
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
