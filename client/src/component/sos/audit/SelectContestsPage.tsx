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

const SelectedContests = (props: any) => {
    const { auditedContests, contests } = props;

    const rows = _.map(props.auditedContests, (auditedContest: any) => {
        const contest = _.find(contests, (c: any) => c.id === auditedContest.id);

        return (
            <tr key={ contest.id }>
                <td>{ contest.id }</td>
                <td>{ contest.name }</td>
                <td>{ auditedContest.reason }</td>
            </tr>
        );
    });

    return (
        <div className='pt-card'>
            <h3>Selected Contests</h3>
            <table className='pt-table pt-bordered pt-condensed'>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Reason</th>
                    </tr>
                </thead>
                <tbody>
                    { rows }
                </tbody>
            </table>
        </div>
    );
};

const SelectContestsPage = (props: any) => {
    const {
        auditedContests,
        back,
        contests,
        nextPage,
        selectContestsForAudit
    } = props;

    const forms: any = {};

    const onSaveAndNext = () => {
        selectContestsForAudit(forms.selectContestsForm);
        nextPage();
    };

    const contentDiv = _.isEmpty(auditedContests)
                     ? <SelectContestsForm forms={ forms } contests={ contests } />
                     : <SelectedContests auditedContests={ auditedContests } contests={ contests } />;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            { contentDiv }
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
