import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createSubmitAction from './createSubmitAction';

import adapter from '../adapter/setRiskLimit';


const url = `http://${apiHost}/risk-limit-comp-audits`;

const setRiskLimit = createSubmitAction({
    failType: 'SET_RISK_LIMIT_FAIL',
    networkFailType: 'SET_RISK_LIMIT_NETWORK_FAIL',
    okType: 'SET_RISK_LIMIT_OK',
    sendType: 'SET_RISK_LIMIT_SEND',
    url,
});


export default (riskLimit: number) => adapter(riskLimit);
