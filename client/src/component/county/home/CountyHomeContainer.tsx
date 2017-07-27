import * as React from 'react';
import { connect } from 'react-redux';

import CountyHomePage from './CountyHomePage';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const { county } = this.props;
        const startAudit = () => this.props.history.push('/county/audit');

        return <CountyHomePage county={ county } startAudit={ startAudit } />;
    }
}

const mapStateToProps = ({ ballotStyles, contests, county }: any) =>
    ({ ballotStyles, contests, county });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyHomeContainer);
