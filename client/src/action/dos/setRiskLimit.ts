import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

import { format } from 'corla/adapter/setRiskLimit';


const url = endpoint('risk-limit-comp-audits');

const setRiskLimit = createSubmitAction({
    failType: 'SET_RISK_LIMIT_FAIL',
    networkFailType: 'SET_RISK_LIMIT_NETWORK_FAIL',
    okType: 'SET_RISK_LIMIT_OK',
    sendType: 'SET_RISK_LIMIT_SEND',
    url,
});


export default (riskLimit: number) => setRiskLimit(format(riskLimit));
