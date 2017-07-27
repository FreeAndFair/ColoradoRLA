import * as React from 'react';
import { connect } from 'react-redux';

import CountyHomePage from './CountyHomePage';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const startAudit = () => this.props.history.push('/county/audit');

        const props = { startAudit, ...this.props };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = ({ ballotStyles, contests, county }: any) =>
    ({ ballotStyles, contests, county });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyHomeContainer);
