import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import * as _ from 'lodash';

import counties from '../../data/counties';

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

    const countyStatuses = _.map(counties, (c: any) => {
        return c;
    });

    return {
        contests: sos.contests,
        counties: sos.counties,
        countyStatuses,
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
