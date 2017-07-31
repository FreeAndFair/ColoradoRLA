import * as React from 'react';

import Nav from '../Nav';

import { NumericInput, Radio, RadioGroup } from '@blueprintjs/core';


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
    const nop = () => ({});

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
                <RadioGroup onChange={ nop } label='Election Type'>
                    <Radio label='Coordinated Election' value='coordinated' />
                    <Radio label='Primary Election' value='primary' />
                </RadioGroup>
            </div>
            <h3>Risk Limits</h3>
            <div>Each contest type has a default risk limit set. To change the risk limit for a
                class, change the percentage shown. To change the risk limit for a particular
                contest use the link below to add an exception from the default.
            </div>
            <div>
                <label>Ballot Polling Audits
                    <NumericInput />
                </label>
            </div>
            <div>
                <label>Comparison Audits
                    <NumericInput />
                </label>
            </div>
        </div>
    );
};


export default Audit;
