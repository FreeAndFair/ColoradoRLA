import * as moment from 'moment-timezone';

import { timezone } from 'corla/config';


export function format(dob: Date): string {
    return moment(dob).format('YYYY-M-D');
}

export function parse(ds: string): Date {
    return moment.tz(ds, timezone).toDate();
}


export default { format, parse };
