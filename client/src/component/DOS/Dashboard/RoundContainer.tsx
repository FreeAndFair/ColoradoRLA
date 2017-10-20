import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import auditStartedSelector from 'corla/selector/dos/auditStarted';
import canStartNextRoundSelector from 'corla/selector/dos/canStartNextRound';
import countiesWithRoundSelector from 'corla/selector/dos/countiesWithRound';
import currentRoundSelector from 'corla/selector/dos/currentRound';


interface ContainerProps {
    auditStarted: boolean;
    canStartNextRound: boolean;
    countiesWithRound: DOS.CountyStatus[];
    currentRound: number;
    dosState: DOS.AppState;
    finishedCountiesCount: number;
    totalCountiesCount: number;
}

class RoundContainer extends React.Component<ContainerProps> {
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

function select(dosState: DOS.AppState) {
    const currentRound = currentRoundSelector(dosState);
    const countiesWithRound = countiesWithRoundSelector(dosState, currentRound);

    const totalCountiesCount = countiesWithRound.length;

    const finished = (c: DOS.CountyStatus) => {
        return c.currentRound.number !== currentRound
            || c.asmState === 'COUNTY_AUDIT_COMPLETE';
    };
    const finishedCountiesCount = countiesWithRound.filter(finished).length;

    return {
        auditStarted: auditStartedSelector(dosState),
        canStartNextRound: canStartNextRoundSelector(dosState),
        countiesWithRound,
        currentRound,
        dosState,
        finishedCountiesCount,
        totalCountiesCount,
    };
}


export default connect(select)(RoundContainer);
