import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import CountyNav from '../Nav';


const WizardStart = () => (
    <div>
        <div>Acme County Audit</div>
        <div className='pt-card'>
            <div>Acme County General Election Audit</div>
            <div>Election date: 11/21/2017</div>
            <div>County & State Ballot Contests</div>
            <button className='pt-button'>Start My Audit</button>
        </div>
    </div>
);

const AuditBoardSignInForm = () => (
    <div>
        <h3>Audit Board Member</h3>
        <div>
            <label>Full Name:
                <EditableText />
            </label>
        </div>
        <div>
            <RadioGroup label='Party Affiliation' onChange={ () => ({}) }>
                <Radio label='Democratic Party' />
                <Radio label='Republican Party' />
                <Radio label='Minor Party' />
                <Radio label='Unaffiliated' />
            </RadioGroup>
        </div>
    </div>
);

const AuditBoardSignIn = () => (
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
        <button className='pt-button pt-intent-primary'>Next</button>
    </div>
);

const CountyAuditWizard = () => (
    <div>
        <WizardStart />
        <AuditBoardSignIn />
    </div>
);

const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizard />
        </div>
    );
};

export default CountyAuditPage;
