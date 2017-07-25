import * as React from 'react';

import CountyNav from '../Nav';


const WizardStart = () => (
    <div>
        <div>Acme County Audit</div>
        <div className='pt-card'>
            <div>Acme County General Election Audit</div>
            <div>Election date: 11/21/2017</div>
            <div>County & State Ballot Contests</div>
            <button className='pt-button'>Start My Audit</button>
        </div>
    </div>
);

const CountyAuditWizard = () => (
    <div>
        <WizardStart />
    </div>
);

const CountyAuditPage = () => {
    return (
        <div>
            <CountyNav />
            <CountyAuditWizard />
        </div>
    );
};

export default CountyAuditPage;
