import * as React from 'react';

import BallotAuditStageContainer from './BallotAuditStageContainer';
import BallotListStageContainer from './BallotListStageContainer';
import ReviewStageContainer from './ReviewStageContainer';
import StartStageContainer from './StartStageContainer';


type WizardStage = 'ballot-audit' | 'list' | 'review' | 'start';

interface CountyAuditWizardState {
    stage: WizardStage;
}

class CountyAuditWizard extends React.Component<any, CountyAuditWizardState> {
    constructor(props: any) {
        super(props);

        this.state = { stage: 'start' };
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
            case 'start':
                return <StartStageContainer { ...props } />;
        }
    }

    private nextStage = () => {
        // tslint:disable
        const t: any = {
            'start': 'list',
            'list': 'ballot-audit',
            'ballot-audit': 'review',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });

        window.scrollTo(0, 0);
    }

    private prevStage = () => {
        // tslint:disable
        const t: any = {
            'start': 'start',
            'list': 'start',
            'ballot-audit': 'list',
            'review': 'ballot-audit',
        };
        // tslint:enable

        const stage = t[this.state.stage];

        this.setState({ stage });

        window.scrollTo(0, 0);
    }
}


export default CountyAuditWizard;
