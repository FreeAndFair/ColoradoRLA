import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import activeCountiesSelector from 'corla/selector/dos/activeCounties';
import auditStartedSelector from 'corla/selector/dos/auditStarted';
import canStartNextRoundSelector from 'corla/selector/dos/canStartNextRound';
import currentRoundSelector from 'corla/selector/dos/currentRound';


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
        activeCounties: activeCountiesSelector(state),
        auditStarted: auditStartedSelector(state),
        canStartNextRound: canStartNextRoundSelector(state),
        currentRound: currentRoundSelector(state),
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
