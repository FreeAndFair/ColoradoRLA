import * as React from 'react';
import { connect } from 'react-redux';

import CountyDetailPage from './CountyDetailPage';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        return <CountyDetailPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyDetailContainer);
