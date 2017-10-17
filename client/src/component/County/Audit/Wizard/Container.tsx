import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './Wizard';


interface WizardContainerProps {
    county: CountyState;
}

class CountyAuditWizardContainer extends React.Component<WizardContainerProps> {
    public render() {
        return <CountyAuditWizard { ...this.props } />;
    }
}

function select({ county }: AppState) {
    return { county };
}


export default connect(select)(CountyAuditWizardContainer);
