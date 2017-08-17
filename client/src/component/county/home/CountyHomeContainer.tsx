import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import CountyHomePage from './CountyHomePage';

import countyDashboardRefresh from '../../../action/countyDashboardRefresh';
import fetchContestsByCounty from '../../../action/fetchContestsByCounty';


const intervalIds: any = {
    refreshId: null,
    fetchContestsId: null,
};

class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const {
            county,
            countyDashboardRefresh,
            fetchContestsByCounty,
            history,
        } = this.props;

        if (!intervalIds.refreshId) {
            countyDashboardRefresh();

            intervalIds.refreshId = setInterval(countyDashboardRefresh, 2000);
        }

        if (!intervalIds.fetchContestsId) {
            fetchContestsByCounty(county.id);

            intervalIds.fetchContestsId = setInterval(
                () => fetchContestsByCounty(county.id),
                1000,
            );
        }

        const startAudit = () => this.props.history.push('/county/audit');

        const props = { startAudit, ...this.props };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    const { contests } = county;

    return { contests, county };
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    countyDashboardRefresh,
    fetchContestsByCounty,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyHomeContainer);
