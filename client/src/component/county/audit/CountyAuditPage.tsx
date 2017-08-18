import * as React from 'react';

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
