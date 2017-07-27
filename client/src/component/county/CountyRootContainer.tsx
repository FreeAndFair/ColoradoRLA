import * as React from 'react';
import { connect } from 'react-redux';

import CountyRootPage from './CountyRootPage';


class CountyRootContainer extends React.Component<any, any> {
    public render() {
        const { county } = this.props;

        return <CountyRootPage { ...county }/>;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyRootContainer);
