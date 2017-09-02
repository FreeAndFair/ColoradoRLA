import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import activeCounties from 'corla/selector/dos/activeCounties';
import auditStarted from 'corla/selector/dos/auditStarted';
import canStartNextRound from 'corla/selector/dos/canStartNextRound';
import currentRound from 'corla/selector/dos/currentRound';


class RoundContainer extends React.Component<any, any> {
    public render() {
        if (this.props.canStartNextRound) {
            return <Control { ...this.props } />;
        }

        if (!this.props.auditStarted) {
            return <div />;
        }

        return <Status { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        activeCounties: activeCounties(state),
        auditStarted: auditStarted(state),
        canStartNextRound: canStartNextRound(state),
        currentRound: currentRound(state),
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
