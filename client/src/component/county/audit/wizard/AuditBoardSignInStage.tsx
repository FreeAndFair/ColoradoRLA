import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';


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

const AuditBoardSignInStage = ({ nextStage }: any) => (
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


export default AuditBoardSignInStage;
