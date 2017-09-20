import * as _ from 'lodash';

import counties from 'corla/data/counties';


type AuditStage
    = 'PRE_AUDIT'
    | 'AUDIT_READY_TO_START'
    | 'AUDIT_ONGOING'
    | 'AUDIT_COMPLETE'
    | 'AUDIT_RESULTS_PUBLISHED';

const sosInitialState = (): any => ({
    asm: {},
    auditStage: 'PRE_AUDIT',
    auditedContests: {},
    countyStatus: {},
});


export default (state: any) => ({
    ...state,
    dashboard: 'sos',
    sos: sosInitialState(),
});
