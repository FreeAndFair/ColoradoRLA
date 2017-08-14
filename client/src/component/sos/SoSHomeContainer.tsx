import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import SoSHomePage from './SoSHomePage';

import dosDashboardRefresh from '../../action/dosDashboardRefresh';
import fetchContests from '../../action/fetchContests';


class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        const { dosDashboardRefresh, fetchContests } = this.props;

        setTimeout(dosDashboardRefresh, 1000);
        setTimeout(fetchContests, 1000);

        return <SoSHomePage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;

    return {
        contests: sos.contests,
        counties: sos.counties,
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
