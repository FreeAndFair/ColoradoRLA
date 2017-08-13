import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import CountyHomePage from './CountyHomePage';

import countyDashboardRefresh from '../../../action/countyDashboardRefresh';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const startAudit = () =>
            this.props.history.push('/county/audit');

        const props = { startAudit, ...this.props };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    const { ballotStyles, contests } = county;

    return { ballotStyles, contests, county };
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    countyDashboardRefresh,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyHomeContainer);
