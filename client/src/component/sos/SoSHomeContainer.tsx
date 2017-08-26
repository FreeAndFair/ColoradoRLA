import * as React from 'react';
import { connect } from 'react-redux';

import * as _ from 'lodash';

import SoSHomePage from './SoSHomePage';


class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        return <SoSHomePage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        contests: sos.contests,
        countyStatus: sos.countyStatus,
        currentAsmState: sos.asm.currentState,
        seed: sos.seed,
        sos,
    };
};


export default connect(mapStateToProps)(SoSHomeContainer);
