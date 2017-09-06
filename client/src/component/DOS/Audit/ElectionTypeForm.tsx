import * as React from 'react';

import { Radio, RadioGroup } from '@blueprintjs/core';



interface FormState {
    type: ElectionType;
}


class ElectionTypeForm extends React.Component<any, FormState> {
    public state: FormState = {
        type: null,
    };

    public render() {
        this.props.forms.electionTypeForm = this.state;

        const { type } = this.state;

        return (
            <div className='pt-card'>
                <RadioGroup
                    selectedValue={ type }
                    onChange={ this.onFormChange }
                    label='Election Type'>
                    <Radio label='Coordinated Election' value='coordinated' />
                    <Radio label='Primary Election' value='primary' />
                    <Radio label='General Election' value='general' />
                    <Radio label='Recall Election' value='recall' />
                </RadioGroup>
            </div>
        );
    }

    private onFormChange = (e: React.ChangeEvent<any>) => {
        const type = e.target.value;

        this.props.setFormValid(!!type);

        this.setState({ type });
    }
}


export default ElectionTypeForm;
