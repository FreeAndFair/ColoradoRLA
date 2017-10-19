import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './Wizard';


interface WizardContainerProps {
    county: County.AppState;
}

class CountyAuditWizardContainer extends React.Component<WizardContainerProps> {
    public render() {
        return <CountyAuditWizard { ...this.props } />;
    }
}

function select(state: AppState) {
    const { county } = state;

    return { county };
}


export default connect(select)(CountyAuditWizardContainer);
