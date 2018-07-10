import * as React from 'react';

import * as moment from 'moment-timezone';

import { DateInput } from '@blueprintjs/datetime';

import { timezone } from 'corla/config';
import corlaDate from 'corla/date';


function defaultElectionDate(initDate: Date): string {

    if (initDate) {
        // date is formatted as utc in corlaDate.format
        // if we don't use utc here we'll get the wrong day sometimes it appears.
        // there may be a better way to do this.
        return moment(initDate).tz('utc').format('YYYY-MM-DD');
    } else {
        return moment.tz(timezone).format('YYYY-MM-DD');
    }
}

interface FormProps {
    forms: DOS.Form.AuditDef.Forms;
    initDate: Date;
}

interface FormState {
    date: string;
}

class ElectionDateForm extends React.Component<FormProps, FormState> {
    public state = {
        date: defaultElectionDate(this.props.initDate),
    };

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
