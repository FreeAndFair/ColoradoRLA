import * as React from 'react';

import * as _ from 'lodash';

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

const formatFormData = (formData: any) => {
    const data: any = [];

    _.forEach(formData, (r, id) => {
        if (r.audit) {
            data.push({
                audit: 'comparison',
                contest: id,
                reason: r.reason.id,
            });
            return;
        }
        if (r.handCount) {
            data.push({
                audit: 'hand_count',
                contest: id,
                reason: 'no_audit',
            });
            return;
        }
    });

    return data;
};

const SelectContestsPage = (props: any) => {
    const { back, contests, nextPage, selectContestsForAudit } = props;

    const forms: any = {};

    const onSaveAndNext = () => {
        selectContestsForAudit(formatFormData(forms.selectContestsForm));
        nextPage();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <SelectContestsForm forms={ forms } contests={ contests } />
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
