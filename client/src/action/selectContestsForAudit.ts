import { Dispatch } from 'redux';

import { apiHost } from '../config';


const selectContestsForAudit = (contests: any[]) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'SELECT_CONTESTS_FOR_AUDIT_SEND' });

        const url = `http://${apiHost}/select-contests`;

        fetch(url, { method: 'post', body: contests })
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'SELECT_CONTESTS_FOR_AUDIT_RECEIVE' });
                } else {
                    dispatch({ type: 'SELECT_CONTESTS_FOR_AUDIT_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL' });
            });
    };
};


export default selectContestsForAudit;
