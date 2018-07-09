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

interface PageProps {
    back: OnClick;
    nextPage: OnClick;
    publicMeetingDate: Date;
    seed: string;
    uploadRandomSeed: OnClick;
}

const AuditSeedPage = (props: PageProps) => {
    const { back, nextPage, publicMeetingDate, seed, uploadRandomSeed } = props;

    const forms: DOS.Form.Seed.Ref = {};

    const onSaveAndNext = () => {
        if (forms.seedForm) {
            uploadRandomSeed(forms.seedForm.seed);
        }

        nextPage();
    };

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
                    <SeedForm forms={ forms } initSeed={ seed } />
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
