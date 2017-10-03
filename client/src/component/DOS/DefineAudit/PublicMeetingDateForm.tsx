import * as React from 'react';

import * as moment from 'moment-timezone';

import { DateInput } from '@blueprintjs/datetime';

import { timezone } from 'corla/config';
import corlaDate from 'corla/date';


function defaultPublicMeetingDate(): string {
    return moment.tz(timezone).add(7, 'days').format('YYYY-MM-DD');
}

class PublicMeetingDateForm extends React.Component<any, any> {
    public state = { date: defaultPublicMeetingDate() };

    public render() {
        this.props.forms.publicMeetingDateForm = this.state;

        const date = this.localDate();

        return (
            <div className='pt-card'>
                <div>Public Meeting Date</div>
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


export default PublicMeetingDateForm;
