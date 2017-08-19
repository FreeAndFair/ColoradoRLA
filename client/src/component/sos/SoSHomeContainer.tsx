import * as React from 'react';
import { connect } from 'react-redux';

import * as _ from 'lodash';

import SoSHomePage from './SoSHomePage';

import dosFetchContests from '../../action/dosFetchContests';


const intervalIds: any = {
    fetchContestsId: null,
};

class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        if (!intervalIds.fetchContestsId) {
            dosFetchContests();

            intervalIds.fetchContestsId = setInterval(dosFetchContests, 2000);
        }

        return <SoSHomePage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        contests: sos.contests,
        countyStatus: sos.countyStatus,
        seed: sos.seed,
        sos,
    };
};


export default connect(mapStateToProps)(SoSHomeContainer);
