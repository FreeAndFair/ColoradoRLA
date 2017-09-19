import * as React from 'react';

import * as moment from 'moment-timezone';

import { DateInput } from '@blueprintjs/datetime';

import { defaultElectionDate } from 'corla/config';

import corlaDate from 'corla/date';


class ElectionDateForm extends React.Component<any, any> {
    public state = { date: defaultElectionDate };

    public render() {
        this.props.forms.electionDateForm = this.state;

        const date = this.localDate();

        return (
            <div className='pt-card'>
                <div>Election Date</div>
                <DateInput value={ date } onChange={ this.onDateChange } />
            </div>
        );
    }

    private onDateChange = (dateObj: Date) => {
        const date = corlaDate.format(dateObj);

        this.setState({ date });
    }

    private localDate(): Date {
        return moment(this.state.date).toDate();
    }
}


export default ElectionDateForm;
