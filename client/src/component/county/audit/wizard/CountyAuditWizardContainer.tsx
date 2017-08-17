import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './CountyAuditWizard';


class CountyAuditWizardContainer extends React.Component<any, any> {
    public render() {
        return <CountyAuditWizard { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyAuditWizardContainer);
