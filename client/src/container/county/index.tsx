import * as React from 'react';
import { connect } from 'react-redux';

import CountyRootPage from '../../component/county';


class CountyRootContainer extends React.Component<any, any> {
    public render() {
        return <CountyRootPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyRootContainer);
