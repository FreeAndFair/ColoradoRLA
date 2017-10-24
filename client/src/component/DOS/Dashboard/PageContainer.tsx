import * as React from 'react';
import { connect } from 'react-redux';

import DOSDashboardPage from './Page';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';


interface ContainerProps {
    contests: DOS.Contests;
    countyStatus: DOS.CountyStatuses;
    seed: string;
    dosState: DOS.AppState;
}

class DOSDashboardContainer extends React.Component<ContainerProps> {
    public render() {
        if (!this.props.dosState) {
            return <div />;
        }

        return <DOSDashboardPage { ...this.props } />;
    }
}

function select(dosState: DOS.AppState) {
    return {
        contests: dosState.contests,
        countyStatus: dosState.countyStatus,
        dosState,
        seed: dosState.seed,
    };
}


export default withPoll(
    withDOSState(DOSDashboardContainer),
    'DOS_DASHBOARD_POLL_START',
    'DOS_DASHBOARD_POLL_STOP',
    select,
);
