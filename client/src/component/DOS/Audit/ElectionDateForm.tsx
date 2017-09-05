import * as React from 'react';

import { DateInput } from '@blueprintjs/datetime';


class ElectionDateForm extends React.Component<any, any> {
    public state = { date: new Date() };

    public render() {
        const { date } = this.state;

        return (
            <div className='pt-card'>
                <div>Election Date</div>
                <DateInput value={ date } onChange={ this.onDateChange } />
            </div>
        );
    }

    private onDateChange = (date: Date) => {
        this.setState({ date });
    }
}


export default ElectionDateForm;
