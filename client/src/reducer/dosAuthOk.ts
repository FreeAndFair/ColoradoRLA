import * as _ from 'lodash';

import counties from '../data/counties';


type AuditStage
    = 'PRE_AUDIT'
    | 'AUDIT_READY_TO_START'
    | 'AUDIT_ONGOING'
    | 'AUDIT_COMPLETE'
    | 'AUDIT_RESULTS_PUBLISHED';


const initialCountyStatus = () => _.mapValues(counties, () => 'NO_DATA');

const sosInitialState = (): any => ({
    auditStage: 'PRE_AUDIT',
    auditedContests: [],
    countyStatus: initialCountyStatus(),
    riskLimit: 0.05,
});


export default (state: any) => ({
    ...state,
    dashboard: 'sos',
    loggedIn: true,
    sos: sosInitialState(),
});
