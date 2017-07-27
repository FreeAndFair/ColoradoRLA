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
        const props = { ...this.props, nextStage: this.nextStage };

        switch (this.state.stage) {
            case 'sign-in':
                return <AuditBoardSignInStage { ...props } />;
            case 'ballot-audit':
                return <BallotAuditStage { ...props } />;
            case 'review':
                return <ReviewStage { ...props } />;
        }
    }

    private nextStage = () => {
        // tslint:disable
        const t: any = {
            'sign-in': 'ballot-audit',
            'ballot-audit': 'review',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });
    }
}

export default CountyAuditWizard;
