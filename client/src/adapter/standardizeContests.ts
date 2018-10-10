import * as _ from 'lodash';

export function format(formData: DOS.Form.StandardizeContests.FormData): JSON.StandardizeContest[] {
    const data: JSON.StandardizeContest[] = [];

    _.forEach(formData, (n, id) => {
        const sc: JSON.StandardizeContest = {
            contest: parseInt(id, 10),
            name: n.name,
        };

        data.push(sc);

        return;
    });

    return data;
}
