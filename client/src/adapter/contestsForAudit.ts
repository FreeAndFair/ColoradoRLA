import * as _ from 'lodash';


export function format(formData: DOS.Form.SelectContests.FormData): JSON.ContestForAudit[] {
    const data: JSON.ContestForAudit[] = [];

    _.forEach(formData, (r, id) => {
        if (r.audit) {
            data.push({
                audit: 'COMPARISON',
                contest: parseInt(id, 10),
                reason: r.reason.id.toUpperCase(),
            });
            return;
        }
        if (r.handCount) {
            data.push({
                audit: 'HAND_COUNT',
                contest: parseInt(id, 10),
                reason: 'NO_AUDIT',
            });
            return;
        }
    });

    return data;
}
