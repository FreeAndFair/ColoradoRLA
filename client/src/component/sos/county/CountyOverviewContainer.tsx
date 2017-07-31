import * as React from 'react';
import { connect } from 'react-redux';

import CountyOverviewPage from './CountyOverviewPage';


class CountyOverviewContainer extends React.Component<any, any> {
    public render() {
        return <CountyOverviewPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyOverviewContainer);
