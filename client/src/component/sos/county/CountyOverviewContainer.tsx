import * as React from 'react';
import { connect } from 'react-redux';

import * as _ from 'lodash';

import counties from 'corla/data/counties';

import CountyOverviewPage from './CountyOverviewPage';


class CountyOverviewContainer extends React.Component<any, any> {
    public render() {
        return <CountyOverviewPage { ...this.props }/>;
    }
}

const mapStateToProps = ({ sos }: any) => {
    const { countyStatus } = sos;

    return { counties, countyStatus, sos };
};

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyOverviewContainer);
