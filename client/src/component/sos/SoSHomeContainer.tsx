import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import * as _ from 'lodash';

import SoSHomePage from './SoSHomePage';

import dosDashboardRefresh from '../../action/dosDashboardRefresh';
import fetchContests from '../../action/fetchContests';


const intervalIds: any = {
    refreshId: null,
    fetchContestsId: null,
};

class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        const { dosDashboardRefresh, fetchContests } = this.props;

        if (!intervalIds.refreshId) {
            dosDashboardRefresh();

            intervalIds.refreshId = setInterval(dosDashboardRefresh, 2000);
        }

        if (!intervalIds.fetchContestsId) {
            fetchContests();

            intervalIds.fetchContestsId = setInterval(fetchContests, 2000);
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

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    dosDashboardRefresh,
    fetchContests,

}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(SoSHomeContainer);
