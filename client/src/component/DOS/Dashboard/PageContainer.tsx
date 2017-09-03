import * as React from 'react';
import { connect } from 'react-redux';

import DOSDashboardPage from './Page';


class DOSDashboardContainer extends React.Component<any, any> {
    public render() {
        return <DOSDashboardPage { ...this.props } />;
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


export default connect(mapStateToProps)(DOSDashboardContainer);
