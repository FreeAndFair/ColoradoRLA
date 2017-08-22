import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';


class AuditBoardSignInForm extends React.Component<any, any> {
    public state = {
        firstName: '',
        lastName: '',
        party: '',
    };

    public render() {
        const { boardMemberIndex, forms } = this.props;

        forms.auditBoard[boardMemberIndex] = this.state;

        const { firstName, lastName, party } = this.state;

        return (
            <div>
                <h3>Audit Board Member</h3>
                <div className='pt-card'>
                    <label>
                        First Name:
                        <EditableText
                            className='pt-input'
                            value={ firstName }
                            onChange={ this.onFirstNameChange } />
                        Last Name:
                        <EditableText
                            className='pt-input'
                            value={ lastName }
                            onChange={ this.onLastNameChange } />
                    </label>
                </div>
                <div className='pt-card'>
                    <RadioGroup
                        label='Party Affiliation'
                        onChange={ this.onPartyChange }
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
    }

    private onFirstNameChange = (name: string) => {
        const s = { ...this.state };

        s.firstName = name

        this.setState(s);
    }

    private onLastNameChange = (name: string) => {
        const s = { ...this.state };

        s.lastName = name;

        this.setState(s);
    }

    private onPartyChange = (e: any) => {
        const s = { ...this.state };

        const party = e.target.value;
        s.party = party;

        this.setState(s);
    }
}


export default AuditBoardSignInForm;
