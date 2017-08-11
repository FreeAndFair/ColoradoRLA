import { Dispatch } from 'redux';

import { apiHost } from '../config';


const setRiskLimit = (riskLimit: number) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'SET_RISK_LIMIT_SEND' });

        const url = `http://${apiHost}/risk-limit-comp-audits`;
        const body = { riskLimit };

        fetch(url, { method: 'post', body })
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'SET_RISK_LIMIT_RECEIVE' });
                } else {
                    dispatch({ type: 'SET_RISK_LIMIT_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'SET_RISK_LIMIT_NETWORK_FAIL' });
            });
    };
};


export default setRiskLimit;
