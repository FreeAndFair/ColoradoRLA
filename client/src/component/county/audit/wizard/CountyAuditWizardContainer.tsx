import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './CountyAuditWizard';


class CountyAuditWizardContainer extends React.Component<any, any> {
    public render() {
        return <CountyAuditWizard { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => ({ county });


export default connect(mapStateToProps)(CountyAuditWizardContainer);
