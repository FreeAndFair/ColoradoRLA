import * as React from 'react';

import { EditableText, Radio, RadioGroup } from '@blueprintjs/core';


class AuditBoardSignInForm extends React.Component<any, any> {
    public state = {
        name: '',
        party: '',
    }

    public render() {
        const { boardMemberIndex, forms, updateBoardMember } = this.props;

        forms.auditBoard[boardMemberIndex] = this.state;

        const { name, party } = this.state;

        return (
            <div>
                <h3>Audit Board Member</h3>
                <div className='pt-card'>
                    <label>Full Name:
                        <EditableText value={ name } onChange={ this.onNameChange } />
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

    private onNameChange = (name: string) => {
        const s = { ...this.state };

        s.name = name;

        this.setState(s);
    };

    private onPartyChange = (e: any) => {
        const s = { ...this.state };

        const party = e.target.value
        s.party = party;

        this.setState(s);
    };
};


export default AuditBoardSignInForm;
