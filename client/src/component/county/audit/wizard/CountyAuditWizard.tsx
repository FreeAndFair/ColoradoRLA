import * as React from 'react';

import AuditBoardSignInStage from './AuditBoardSignInStage';
import BallotAuditStage from './BallotAuditStage';
import ReviewStage from './ReviewStage';


type WizardStage = 'sign-in' | 'ballot-audit' | 'review';

interface CountyAuditWizardState {
    stage: WizardStage;
}

class CountyAuditWizard extends React.Component<any, CountyAuditWizardState> {
    constructor(props: any) {
        super(props);

        this.state = { stage: 'sign-in' };
    }

    public render() {
        let nextStage;

        switch (this.state.stage) {
            case 'sign-in':
                nextStage = () => this.setState({ stage: 'ballot-audit' });
                return <AuditBoardSignInStage nextStage={ nextStage } />;
            case 'ballot-audit':
                nextStage = () => this.setState({ stage: 'review' });
                return <BallotAuditStage nextStage={ nextStage } />;
            case 'review':
                nextStage = () => this.setState({ stage: 'ballot-audit' });
                return <ReviewStage nextStage={ nextStage } />;
        }
    }
}

export default CountyAuditWizard;
