import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';


const AuditBoardSignInForm = (props: any) => {
    const { boardMember } = props;
    const { name, party } = boardMember;

    return (
        <div>
            <h3>Audit Board Member</h3>
            <div className='pt-card'>
                <label>Full Name:
                    <EditableText value={ name } />
                </label>
            </div>
            <div className='pt-card'>
                <RadioGroup
                    label='Party Affiliation'
                    onChange={ () => ({}) }
                    selectedValue={ party }
                >
                    <Radio
                        label='Democratic Party'
                        value='Democratic Party'
                    />
                    <Radio
                        label='Republican Party'
                        value='Republican Party'
                    />
                    <Radio
                        label='Other Party'
                        value='Other Party'
                    />
                    <Radio
                        label='Unaffiliated'
                        value='Unaffiliated'
                    />
                </RadioGroup>
            </div>
        </div>
    );
};

const AuditBoardSignInStage = (props: any) => {
    const { county, nextStage } = props;
    const { auditBoard } = county;

    return (
        <div>
            <div>
                <h2>Audit Board Sign-in</h2>
                <p>Enter the full names and party affiliations of each member of
                    the Acme County Audit Board who will be conducting this audit
                    today:
                </p>
            </div>
            <AuditBoardSignInForm boardMember={ auditBoard[0] } />
            <AuditBoardSignInForm boardMember={ auditBoard[1] } />
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Next
            </button>
        </div>
    );
};


export default AuditBoardSignInStage;
