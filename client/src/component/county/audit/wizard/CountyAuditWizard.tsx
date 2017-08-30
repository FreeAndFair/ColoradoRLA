import * as React from 'react';

import BallotAuditStageContainer from './BallotAuditStageContainer';
import BallotListStageContainer from './BallotListStageContainer';
import ReviewStageContainer from './ReviewStageContainer';


type WizardStage = 'ballot-audit' | 'list' | 'review';

interface CountyAuditWizardState {
    stage: WizardStage;
}

class CountyAuditWizard extends React.Component<any, CountyAuditWizardState> {
    constructor(props: any) {
        super(props);

        this.state = { stage: 'list' };
    }

    public render() {
        const { nextStage, prevStage } = this;

        const props = {
            ...this.props,
            nextStage,
            prevStage,
        };

        switch (this.state.stage) {
            case 'ballot-audit':
                return <BallotAuditStageContainer { ...props } />;
            case 'list':
                return <BallotListStageContainer { ...props } />;
            case 'review':
                return <ReviewStageContainer { ...props } />;
        }
    }

    private nextStage = () => {
        // tslint:disable
        const t: any = {
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
            'ballot-audit': 'ballot-audit',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });
    }
}

export default CountyAuditWizard;
