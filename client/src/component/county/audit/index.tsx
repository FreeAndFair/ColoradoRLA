import * as React from 'react';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import CountyNav from '../Nav';


const WizardStart = ({ nextStage }: any) => (
    <div>
        <div>Acme County Audit</div>
        <div className='pt-card'>
            <div>Acme County General Election Audit</div>
            <div>Election date: 11/21/2017</div>
            <div>County & State Ballot Contests</div>
            <button className='pt-button' onClick={ nextStage }>
                Start My Audit
            </button>
        </div>
    </div>
);

const AuditBoardSignInForm = () => (
    <div>
        <h3>Audit Board Member</h3>
        <div className='pt-card'>
            <label>Full Name:
                <EditableText />
            </label>
        </div>
        <div className='pt-card'>
            <RadioGroup label='Party Affiliation' onChange={ () => ({}) }>
                <Radio label='Democratic Party' />
                <Radio label='Republican Party' />
                <Radio label='Minor Party' />
                <Radio label='Unaffiliated' />
            </RadioGroup>
        </div>
    </div>
);

const AuditBoardSignIn = ({ nextStage }: any) => (
    <div>
        <div>
            <h2>Audit Board Sign-in</h2>
            <p>Enter the full names and party affiliations of each member of
                the Acme County Audit Board who will be conducting this audit
                today:
            </p>
        </div>
        <AuditBoardSignInForm />
        <AuditBoardSignInForm />
        <button className='pt-button pt-intent-primary' onClick={ nextStage }>
            Next
        </button>
    </div>
);

const AuditInstructions = () => (
    <div>
        <div>
            Use this page to report the voter markings on the <span>2nd</span>
            of <span>54</span> balots that you must audit.
        </div>
        <div>
            The <span>2nd</span> ballot is:
            <ul>
                <li>[ballot ID]</li>
                <li>[ballot style name]</li>
            </ul>
            <div>
                Please ensure that the paper ballot you are examining is the
                same ballot style/ID.
            </div>
            <div>
                Replicate on this page all valid votes in each ballot contest
                contained on this paper ballot.  If you determine a particular
                ballot contest does not contain a valid vote, please also
                indicate whether the ballot contains a blank vote or an over
                vote.
            </div>
            <div>
                If the paper ballot you are examining indicates that it was
                duplicated, please ask a county staff member to retrieve the
                original paper ballot submitted by the voter, and report the
                votes contained on that ballot on this page.
            </div>
        </div>
    </div>
);

const BallotContest = () => (
    <div className='pt-card'>
        <div className='pt-card'>
            <div>Ballot contest [N]</div>
            <div>Acme County School District RE-1</div>
            <div>Director</div>
            <div>
                <div>Vote for [N]</div>
            </div>
        </div>
        <div>
            <Checkbox checked={ false } onChange={ () => ({}) } label='Choice A' />
            <Checkbox checked={ false } onChange={ () => ({}) } label='Choice B' />
            <Checkbox checked={ false } onChange={ () => ({}) } label='Choice C' />
        </div>
        <div>
            <RadioGroup label='' onChange={ () => ({}) }>
                <Radio label='Undervote' />
                <Radio label='No consensus' />
            </RadioGroup>
        </div>
        <div className='pt-card'>
            <label>
                Comments:
                <EditableText multiline/>
            </label>
        </div>
    </div>
);

const BallotContests = () => (
    <div>
        <BallotContest />
        <BallotContest />
        <BallotContest />
    </div>
);

const BallotAudit = ({ nextStage }: any) => (
    <div>
        <h2>Ballot verification</h2>
        <AuditInstructions />
        <BallotContests />
        <button className='pt-button pt-intent-primary' onClick={ nextStage }>
            Review
        </button>
    </div>
);

type WizardStage = 'start' | 'sign-in' | 'ballot';

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
                nextStage = () => this.setState({ stage: 'ballot' });
                return <AuditBoardSignIn nextStage={ nextStage } />;
            case 'ballot':
                nextStage = () => this.setState({ stage: 'start' });
                return <BallotAudit nextStage={ nextStage } />;
        }
    }
}

const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizard />
        </div>
    );
};

export default CountyAuditPage;
