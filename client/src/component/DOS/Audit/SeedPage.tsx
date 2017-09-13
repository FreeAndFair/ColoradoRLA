import * as React from 'react';

import Nav from '../Nav';

import SeedForm from './SeedForm';

import * as corlaDate from 'corla/date';


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


const AuditSeedPage = (props: any) => {
    const { back, nextPage, publicMeetingDate, seed, uploadRandomSeed } = props;

    const forms: any = {};

    const onSaveAndNext = () => {
        if (forms.seedForm) {
            uploadRandomSeed(forms.seedForm.seed);
        }

        nextPage();
    };

    const setSeedDiv = (
        <div>
            Random seed: { seed }
        </div>
    );

    const dynamicSeedForm = <SeedForm forms={ forms } />;
    const seedForm = seed ? setSeedDiv : dynamicSeedForm;

    const formattedPublicMeetingDate = corlaDate.format(publicMeetingDate);

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div className='pt-card'>
                <h3>Audit Definition - Enter Random Seed</h3>
                <div className='pt-card'>
                    Enter the random seed generated from the public meeting on { formattedPublicMeetingDate }.
                </div>
                <div className='pt-card'>
                    <span className='pt-icon pt-intent-warning pt-icon-warning-sign' />
                    <span> </span>
                    Once saved, this random seed cannot be modified.
                </div>
                <div className='pt-card'>
                    { seedForm }
                </div>
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
