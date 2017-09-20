import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import auditStartedSelector from 'corla/selector/dos/auditStarted';
import canStartNextRoundSelector from 'corla/selector/dos/canStartNextRound';
import countiesWithRoundSelector from 'corla/selector/dos/countiesWithRound';
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

    const currentRound = currentRoundSelector(state);

    return {
        auditStarted: auditStartedSelector(state),
        canStartNextRound: canStartNextRoundSelector(state),
        countiesWithRound: countiesWithRoundSelector(state, currentRound),
        currentRound,
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
