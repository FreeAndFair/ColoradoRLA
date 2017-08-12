import * as React from 'react';

import { EditableText } from '@blueprintjs/core';


class SeedForm extends React.Component<any, any> {
    public state = { seed: '' };

    public render() {
        const { seed } = this.state;

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
