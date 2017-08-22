import { endpoint } from '../config';

import createSubmitAction from './next/createSubmitAction';

import { format } from '../adapter/setRiskLimit';


const url = endpoint('risk-limit-comp-audits');

const setRiskLimit = createSubmitAction({
    failType: 'SET_RISK_LIMIT_FAIL',
    networkFailType: 'SET_RISK_LIMIT_NETWORK_FAIL',
    okType: 'SET_RISK_LIMIT_OK',
    sendType: 'SET_RISK_LIMIT_SEND',
    url,
});


export default (riskLimit: number) => setRiskLimit(format(riskLimit));
