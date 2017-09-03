import * as React from 'react';

import { Radio, RadioGroup } from '@blueprintjs/core';


type ElectionType = 'coordinated'
                  | 'primary'
                  | 'general'
                  | 'recall';

interface FormState {
    electionType: ElectionType;
}


class ElectionTypeForm extends React.Component<any, FormState> {
    public state: FormState = {
        electionType: null,
    };

    public render() {
        const { electionType } = this.state;

        return (
            <RadioGroup
                selectedValue={ electionType }
                onChange={ this.onFormChange }
                label='Election Type'>
                <Radio label='Coordinated Election' value='coordinated' />
                <Radio label='Primary Election' value='primary' />
                <Radio label='General Election' value='general' />
                <Radio label='Recall Election' value='recall' />
            </RadioGroup>
        );
    }

    private onFormChange = (e: React.ChangeEvent<any>) => {
        const electionType = e.target.value;
        this.setState({ electionType });
    }
}


export default ElectionTypeForm;
