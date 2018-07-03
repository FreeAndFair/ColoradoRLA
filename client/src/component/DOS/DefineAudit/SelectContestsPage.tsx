import * as React from 'react';

import * as _ from 'lodash';

import Nav from '../Nav';

import SelectContestsForm from './SelectContestsForm';

import counties from 'corla/data/counties';


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

function formatReason(reason: AuditReason): string {
    if (reason === 'STATE_WIDE_CONTEST') {
        return 'State Contest';
    }

    return 'County Contest';
}

interface SelectedContestsProps {
    auditedContests: DOS.AuditedContests;
    contests: DOS.Contests;
}

export const SelectedContests = (props: SelectedContestsProps) => {
    const { auditedContests, contests } = props;

    const rows = _.map(props.auditedContests, audited => {
        const contest = contests[audited.id];
        const countyName = counties[contest.countyId].name;

        return (
            <tr key={ contest.id }>
                <td>{ countyName }</td>
                <td>{ contest.name }</td>
                <td>{ formatReason(audited.reason) }</td>
            </tr>
        );
    });

    return (
        <div className='pt-card'>
            <h3>Selected Contests</h3>
            <div className='pt-card'>
                <table className='pt-table pt-bordered pt-condensed'>
                    <thead>
                        <tr>
                            <th>County</th>
                            <th>Name</th>
                            <th>Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        { rows }
                    </tbody>
                </table>
            </div>
        </div>
    );
};

interface WaitingPageProps {
    back: OnClick;
}

const WaitingForContestsPage = ({ back }: WaitingPageProps) => {
    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div className='pt-card'>
                Waiting for counties to upload contest data.
            </div>
            <div>
                <button onClick={ back } className='pt-button'>
                    Back
                </button>
                <button disabled className='pt-button pt-intent-primary'>
                    Save & Next
                </button>
            </div>
        </div>
    );
};

interface PageProps {
    auditedContests: DOS.AuditedContests;
    back: OnClick;
    contests: DOS.Contests;
    isAuditable: OnClick;
    nextPage: OnClick;
    selectContestsForAudit: OnClick;
}

const SelectContestsPage = (props: PageProps) => {
    const {
        auditedContests,
        back,
        contests,
        isAuditable,
        nextPage,
        selectContestsForAudit,
    } = props;

    if (_.isEmpty(contests)) {
        return <WaitingForContestsPage back={ back } />;
    }

    const forms: DOS.Form.SelectContests.Ref = {};

    const haveSelectedContests = !_.isEmpty(auditedContests);

    const onSaveAndNext = () => {
        if (!haveSelectedContests) {
            selectContestsForAudit(forms.selectContestsForm);
        }
        nextPage();
    };

    const contentDiv = !haveSelectedContests
                     ? <SelectContestsForm forms={ forms }
                                           contests={ contests }
                                           isAuditable={ isAuditable } />
                     : <SelectedContests auditedContests={ auditedContests }
                                         contests={ contests } />;

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
