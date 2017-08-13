import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import SoSHomePage from './SoSHomePage';

import dosDashboardRefresh from '../../action/dosDashboardRefresh';


class SoSHomeContainer extends React.Component<any, any> {
    public render() {
        return <SoSHomePage { ...this.props } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({
    contests: sos.contests,
    counties: sos.counties,
    seed: sos.seed,
    sos,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    dosDashboardRefresh,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(SoSHomeContainer);
