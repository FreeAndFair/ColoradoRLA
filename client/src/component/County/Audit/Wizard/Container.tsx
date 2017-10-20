import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './Wizard';


interface WizardContainerProps {
    countyState: County.AppState;
}

class CountyAuditWizardContainer extends React.Component<WizardContainerProps> {
    public render() {
        return <CountyAuditWizard { ...this.props } />;
    }
}

function select(countyState: AppState) {
    return { countyState };
}


export default connect(select)(CountyAuditWizardContainer);
