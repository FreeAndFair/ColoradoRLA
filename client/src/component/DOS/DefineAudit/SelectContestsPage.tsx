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
        selectContestsForAudit(forms.selectContestsForm);
        nextPage();
    };

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <SelectContestsForm forms={ forms }
                                contests={ contests }
                                auditedContests={auditedContests}
                                isAuditable={ isAuditable } />

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
