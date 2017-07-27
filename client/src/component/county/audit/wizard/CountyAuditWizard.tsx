import * as React from 'react';

import AuditBoardSignInStage from './AuditBoardSignInStage';
import BallotAuditStage from './BallotAuditStage';
import ReviewStage from './ReviewStage';
import StartStage from './StartStage';


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
                return <StartStage nextStage={ nextStage } />;
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
