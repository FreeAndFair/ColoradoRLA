import * as React from 'react';

import { connect } from 'react-redux';

import Control from './Round/Control';
import Status from './Round/Status';


class RoundContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Round</div>
        );
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        sos,
    };
};


export default connect(mapStateToProps)(RoundContainer);
