import * as React from 'react';

import { NumericInput, Radio, RadioGroup } from '@blueprintjs/core';

import Nav from '../Nav';

import ElectionDateForm from './ElectionDateForm';


const nop = () => ({});

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

const ElectionTypeForm = () => {
    return (
        <div>
            <RadioGroup onChange={ nop } label='Election Type'>
                <Radio label='Coordinated Election' value='coordinated' />
                <Radio label='Primary Election' value='primary' />
            </RadioGroup>
        </div>
    );
};

const RiskLimitForm = () => {
    return (
        <div>
            <div>
                <label>
                    Ballot Polling Audits
                    <NumericInput />
                </label>
            </div>
            <div>
                <label>
                    Comparison Audits
                    <NumericInput />
                </label>
            </div>
        </div>
    );
};

const AuditPage = ({ saveAndNext }: any) => {
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
            <button onClick={ saveAndNext } className='pt-button pt-intent-primary'>
                Save
            </button>
        </div>
    );
};


export default AuditPage;
