import * as React from 'react';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import CountyNav from '../Nav';

import CountyAuditWizardContainer from './wizard/CountyAuditWizardContainer';


const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizardContainer />
        </div>
    );
};


export default CountyAuditPage;
