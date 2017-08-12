import * as React from 'react';

import Nav from '../Nav';

import SeedForm from './SeedForm';


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
                Seed
            </a>
        </li>
    </ul>
);

const AuditSeedPage = ({ back, nextPage }: any) => {
    const onSaveAndNext = () => {
        nextPage();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div>
                <h3>Audit Definition - Enter Random Seed</h3>
                <div>Enter the random seed generated from the public meeting on 11/10/2017.</div>

                <h4>Audit Random Seed</h4>
                <div>Please enter the seed generated from the public meeting.</div>
                <SeedForm />
            </div>
            <div>
                <button onClick={ back } className='pt-button'>
                    Back
                </button>
                <button onClick={ onSaveAndNext } className='pt-button pt-intent-primary'>
                    Save & Next
                </button>
            </div>
        </div>
    );
};


export default AuditSeedPage;
