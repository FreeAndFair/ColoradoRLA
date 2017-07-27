import * as React from 'react';

import AuditBoardSignIn from './AuditBoardSignIn';
import BallotAudit from './BallotAudit';
import WizardStart from './WizardStart';


type WizardStage = 'start' | 'sign-in' | 'ballot-audit' | 'review';

interface CountyAuditWizardState {
    stage: WizardStage;
}

class CountyAuditWizard extends React.Component<any, CountyAuditWizardState> {
    constructor(props: any) {
        super(props);

        this.state = { stage: 'start' };
    }

    public render() {
        let nextStage;

        switch (this.state.stage) {
            case 'start':
                nextStage = () => this.setState({ stage: 'sign-in' });
                return <WizardStart nextStage={ nextStage } />;
            case 'sign-in':
                nextStage = () => this.setState({ stage: 'ballot-audit' });
                return <AuditBoardSignIn nextStage={ nextStage } />;
            case 'ballot-audit':
                nextStage = () => this.setState({ stage: 'review' });
                return <BallotAudit nextStage={ nextStage } />;
            case 'review':
                nextStage = () => this.setState({ stage: 'ballot-audit' });
                return <BallotAudit nextStage={ nextStage } />;
        }
    }
}

export default CountyAuditWizard;
