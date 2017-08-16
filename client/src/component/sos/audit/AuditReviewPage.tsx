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
            <a className='pt-breadcrumb' href='/sos/audit'>
                Audit Admin
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Review
            </a>
        </li>
    </ul>
);


const AuditReview = ({ back, publishBallotsToAudit, saveAndDone }: any) => {
    const launch = () => {
        publishBallotsToAudit();
        saveAndDone();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Audit</h2>
            <h3>Audit Definition Review</h3>
            <div>
                This is the set of audit data which will be used to define the list of
                ballots to audit for each county. Once this is submitted, it will be released
                to the counties and the previous pages will not be editable.
            </div>
            <div>
                <button onClick={ back } className='pt-button'>
                    Back
                </button>
                <button onClick={ saveAndDone } className='pt-button pt-intent-primary'>
                    Launch Audit
                </button>
            </div>
        </div>
    );
};


export default AuditReview;
