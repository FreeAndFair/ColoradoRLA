import * as React from 'react';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import CountyNav from '../Nav';

import CountyAuditWizard from './wizard/CountyAuditWizard';


const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizard />
        </div>
    );
};


export default CountyAuditPage;
