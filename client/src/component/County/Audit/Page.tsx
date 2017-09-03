import * as React from 'react';

import CountyNav from '../Nav';

import CountyAuditWizardContainer from './Wizard/Container';


const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizardContainer />
        </div>
    );
};


export default CountyAuditPage;
