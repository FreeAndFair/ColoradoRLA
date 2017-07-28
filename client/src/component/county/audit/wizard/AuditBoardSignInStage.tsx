import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';


const AuditBoardSignInForm = (props: any) => {
    const { boardMember, updateBoardMember } = props;
    const { index, name, party } = boardMember;

    const onNameChange = (nextName: string) => {
        updateBoardMember(index, nextName, party);
    };

    const onPartyChange = (e: any) => {
        const nextParty = e.target.value;
        updateBoardMember(index, name, nextParty);
    };

    return (
        <div>
            <h3>Audit Board Member</h3>
            <div className='pt-card'>
                <label>Full Name:
                    <EditableText value={ name } onChange={ onNameChange } />
                </label>
            </div>
            <div className='pt-card'>
                <RadioGroup
                    label='Party Affiliation'
                    onChange={ onPartyChange }
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
    const { auditBoard, county, nextStage, updateBoardMember } = props;

    const boardMember = (i: number) => ({ index: i, ...auditBoard[i] });

    return (
        <div>
            <div>
                <h2>Audit Board Sign-in</h2>
                <p>Enter the full names and party affiliations of each member of
                    the Acme County Audit Board who will be conducting this audit
                    today:
                </p>
            </div>
            <AuditBoardSignInForm
                boardMember={ boardMember(0) }
                updateBoardMember={ updateBoardMember }
            />
            <AuditBoardSignInForm
                boardMember={ boardMember(1) }
                updateBoardMember={ updateBoardMember }
            />
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Next
            </button>
        </div>
    );
};


export default AuditBoardSignInStage;
