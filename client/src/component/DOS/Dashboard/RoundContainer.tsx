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

const select = (state: any) => {
    const { sos } = state;

    if (!sos) {
        return {};
    }

    const currentRound = currentRoundSelector(state);
    const countiesWithRound = countiesWithRoundSelector(state, currentRound);

    const totalCountiesCount = countiesWithRound.length;

    const finished = (c: any) => {
        return c.currentRound.number !== currentRound
            || c.asmState === 'COUNTY_AUDIT_COMPLETE';
    };
    const finishedCountiesCount = countiesWithRound.filter(finished).length;

    return {
        auditStarted: auditStartedSelector(state),
        canStartNextRound: canStartNextRoundSelector(state),
        countiesWithRound,
        currentRound,
        finishedCountiesCount,
        sos,
        totalCountiesCount,
    };
};


export default connect(select)(RoundContainer);
