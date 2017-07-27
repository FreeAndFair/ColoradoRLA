import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditPage from './audit/CountyAuditPage';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        return <CountyAuditPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyAuditContainer);
