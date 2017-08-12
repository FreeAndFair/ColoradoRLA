import * as React from 'react';

import Nav from '../Nav';

import ElectionDateForm from './ElectionDateForm';
import ElectionTypeForm from './ElectionTypeForm';
import RiskLimitForm from './RiskLimitForm';


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

const AuditPage = ({ nextPage }: any) => {
    const buttonClick = () => {
        nextPage();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />

            <h2>Administer an Audit</h2>

            <h3>Audit Definition</h3>
            <div>Enter the date the election will take place, and the type of election.</div>
            <ElectionDateForm />
            <ElectionTypeForm />

            <h3>Risk Limits</h3>
            <div>Each contest type has a default risk limit set. To change the risk limit for a
                class, change the percentage shown. To change the risk limit for a particular
                contest use the link below to add an exception from the default.
            </div>
            <RiskLimitForm />
            <div>
                Once the election has started, this information will not be able to be changed.
            </div>

            <button onClick={ buttonClick } className='pt-button pt-intent-primary'>
                Save
            </button>
        </div>
    );
};


export default AuditPage;
