import * as React from 'react';

import AuditBoardSignInStageContainer from './AuditBoardSignInStageContainer';
import BallotAuditStageContainer from './BallotAuditStageContainer';
import ReviewStageContainer from './ReviewStageContainer';


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
        const { nextStage, prevStage } = this;

        const props = {
            ...this.props,
            nextStage,
            prevStage,
        };

        switch (this.state.stage) {
            case 'sign-in':
                return  <AuditBoardSignInStageContainer { ...props } />;
            case 'ballot-audit':
                return <BallotAuditStageContainer { ...props } />;
            case 'review':
                return <ReviewStageContainer { ...props } />;
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

    private prevStage = () => {
        // tslint:disable
        const t: any = {
            'ballot-audit': 'sign-in',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });
    }
}

export default CountyAuditWizard;
