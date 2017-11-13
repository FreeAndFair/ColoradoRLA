import * as React from 'react';

import { Radio, RadioGroup } from '@blueprintjs/core';

import * as format from 'corla/format';


const ELECTION_TYPES: ElectionType[] =
    ['coordinated', 'primary', 'general', 'recall'];

interface FormProps {
    forms: DOS.Form.AuditDef.Forms;
    setFormValid: OnClick;
}

interface FormState {
    type?: ElectionType;
}

class ElectionTypeForm extends React.Component<FormProps, FormState> {
    public state: FormState = {
        type: undefined,
    };

    public render() {
        this.props.forms.electionTypeForm = this.state;

        const { type } = this.state;

        const radios = ELECTION_TYPES.map(ty => {
            const label = format.electionType(ty);

            return <Radio key={ ty } label={ label } value={ ty } />;
        });

        return (
            <div className='pt-card'>
                <RadioGroup
                    className='rla-radio-group'
                    selectedValue={ type }
                    onChange={ this.onFormChange }
                    label='Election Type'>
                    { radios }
                </RadioGroup>
            </div>
        );
    }

    private onFormChange = (e: React.ChangeEvent<any>) => {
        const type = e.target.value;

        this.props.setFormValid({ type: !!type });

        this.setState({ type });
    }
}


export default ElectionTypeForm;
