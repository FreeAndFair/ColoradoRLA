import * as React from 'react';
import { connect } from 'react-redux';

import AuditPage from './AuditPage';


class AuditContainer extends React.Component<any, any> {
    public render() {
        const nextPage = () => this.props.history.push('/sos/audit/seed');

        return <AuditPage nextPage={ nextPage } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditContainer);
