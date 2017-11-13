import * as React from 'react';

import { EditableText } from '@blueprintjs/core';


interface FormProps {
    forms: DOS.Form.Seed.Ref;
}

interface FormState {
    seed: string;
}


class SeedForm extends React.Component<FormProps, FormState> {
    public state = { seed: '' };

    public render() {
        const { seed } = this.state;

        this.props.forms.seedForm = this.state;

        return (
            <label>
                Seed
                <EditableText
                    className='pt-input'
                    minWidth={ 64 }
                    value={ seed }
                    onChange={ this.onSeedChange } />
            </label>
        );
    }

    private onSeedChange = (seed: string) => {
        this.setState({ seed });
    }
}


export default SeedForm;
