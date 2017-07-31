import * as React from 'react';

import Nav from '../Nav';


const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Audit Admin
            </a>
        </li>
    </ul>
);


const Audit = () => {
    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Administer an Audit</h2>
            <h3>Audit Definition</h3>
            <div>Enter the date the election will take place, and the type of election.</div>
            <div>
                Election date.
            </div>
            <div>
                Election type.
            </div>
            <h3>Risk Limits</h3>
            <div>Each contest type has a default risk limit set. To change the risk limit for a
                class, change the percentage shown. To change the risk limit for a particular
                contest use the link below to add an exception from the default.
            </div>
            <h4>Ballot Polling Audits</h4>
            <h4>Comparison Audits</h4>
        </div>
    );
};


export default Audit;
