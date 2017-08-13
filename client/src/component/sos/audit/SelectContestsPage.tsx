import * as React from 'react';

import Nav from '../Nav';

import SelectContestsForm from './SelectContestsForm';


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
                Select Contests
            </a>
        </li>
    </ul>
);


const SelectContestsPage = ({ back, contests, nextPage }: any) => {
    const onSaveAndNext = () => {
        nextPage();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <SelectContestsForm contests={ contests } />
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


export default SelectContestsPage;
