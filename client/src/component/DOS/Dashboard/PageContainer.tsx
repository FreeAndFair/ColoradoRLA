import * as React from 'react';
import { connect } from 'react-redux';

import DOSDashboardPage from './Page';

import action from 'corla/action';


class DOSDashboardContainer extends React.Component<any, any> {
    public componentDidMount() {
        action('DOS_DASHBOARD_POLL');
    }

    public render() {
        return <DOSDashboardPage { ...this.props } />;
    }
}

const select = (state: any) => {
    const { sos } = state;

    return {
        contests: sos.contests,
        countyStatus: sos.countyStatus,
        seed: sos.seed,
        sos,
    };
};


export default connect(select)(DOSDashboardContainer);
