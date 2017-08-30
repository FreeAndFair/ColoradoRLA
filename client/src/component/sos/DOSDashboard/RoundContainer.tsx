import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';

import canStartNextRound from '../../../selector/dos/canStartNextRound';


class RoundContainer extends React.Component<any, any> {
    public render() {
        if (this.props.canStartNextRound) {
            return <Control />;
        }

        return <Status />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        canStartNextRound: canStartNextRound(state),
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
