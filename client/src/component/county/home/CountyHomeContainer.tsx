import * as React from 'react';
import { connect } from 'react-redux';

import CountyHomePage from './CountyHomePage';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const { county } = this.props;

        return <CountyHomePage { ...county }/>;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyHomeContainer);
