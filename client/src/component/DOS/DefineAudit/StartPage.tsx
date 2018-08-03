import * as React from 'react';

import corlaDate from 'corla/date';

import Nav from '../Nav';

import ElectionDateForm from './ElectionDateForm';
import ElectionTypeForm from './ElectionTypeForm';
import PublicMeetingDateForm from './PublicMeetingDateForm';
import RiskLimitForm from './RiskLimitForm';
import UploadFileButton from './UploadFileButton';

import setAuditInfo from 'corla/action/dos/setAuditInfo';

import { timezone } from 'corla/config';
import * as format from 'corla/format';


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

function round(val: number, digits: number) {
    const factor = Math.pow(10, digits);
    return Math.round(val * factor) / factor;
}

interface SaveButtonProps {
    disabled: boolean;
    forms: DOS.Form.AuditDef.Forms;
    nextPage: OnClick;
}

const SaveButton = (props: SaveButtonProps) => {
    const { disabled, forms, nextPage } = props;

    const buttonClick = () => {
        if (!forms.electionDateForm) { return; }
        if (!forms.electionTypeForm) { return; }
        if (!forms.publicMeetingDateForm) { return; }
        if (!forms.uploadFile) { return; }

        if (!forms.electionDateForm.date) { return; }
        if (!forms.electionTypeForm.type) { return; }
        if (!forms.publicMeetingDateForm.date) { return; }

        const electionDate = corlaDate.parse(forms.electionDateForm.date);
        const { type } = forms.electionTypeForm;
        const publicMeetingDate = corlaDate.parse(forms.publicMeetingDateForm.date);

        if (!forms.riskLimit) { return; }
        const riskLimit = forms.riskLimit.comparisonLimit;

        const uploadFile = forms.uploadFile.files.map((file: any) => file);

        setAuditInfo({
            election: {
                date: electionDate,
                type,
            },
            publicMeetingDate,
            riskLimit,
            uploadFile,
        });
        nextPage();
    };

    return (
        <button
            disabled={ disabled }
            onClick={ buttonClick }
            className='pt-button pt-intent-primary'>
            Save & Next
        </button>
    );
};


interface PageProps {
    election: Election;
    formValid: boolean;
    nextPage: OnClick;
    publicMeetingDate: Date;
    riskLimit: number;
    setFormValid: OnClick;
}

const AuditPage = (props: PageProps) => {
    const {
        election,
        formValid,
        nextPage,
        publicMeetingDate,
        riskLimit,
        setFormValid,
    } = props;

    const electionAndRiskLimitSet = riskLimit
                                 && election
                                 && election.date
                                 && election.type;

    const forms: DOS.Form.AuditDef.Forms = {};

    const disableButton = !formValid;

    return (
        <div>
            <Nav />
            <Breadcrumb />

            <h2>Administer an Audit</h2>

            <div className='pt-card'>
                <h3>Election Info</h3>
                <div>Enter the date the election will take place, and the type of election.</div>
                <ElectionDateForm forms={ forms } initDate={election && election.date} />
                <ElectionTypeForm forms={ forms } initType={election && election.type} setFormValid={ setFormValid } />
            </div>

            <div className='pt-card'>
                <h3>Public Meeting Date</h3>
                <div>Enter the date of the public meeting to establish the random seed.</div>
                <PublicMeetingDateForm forms={ forms } initDate={ publicMeetingDate } />
            </div>

            <div className='pt-card'>
                <h3>Risk Limit</h3>
                <div>
                  <strong>Enter the risk limit for comparison audits as a percentage.</strong>
                </div>
                <RiskLimitForm forms={ forms }
                               riskLimit={ riskLimit }
                               setFormValid={ setFormValid } />

            </div>

            <div className='pt-card'>
                <h3>Contests</h3>
                <UploadFileButton forms={ forms } />
            </div>

            <div className='control-buttons'>
              <SaveButton disabled={ disableButton }
                          forms={ forms }
                          nextPage={ nextPage } />
            </div>
        </div>
    );
};


export default AuditPage;
