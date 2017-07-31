import * as React from 'react';
import { connect } from 'react-redux';

import CountyOverviewPage from './CountyOverviewPage';


class CountyOverviewContainer extends React.Component<any, any> {
    public render() {
        return <CountyOverviewPage { ...this.props }/>;
    }
}

const mapStateToProps = (state: any) => {
    const { sos } = state;
    const { counties } = sos;

    return { counties, sos };
};

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyOverviewContainer);
