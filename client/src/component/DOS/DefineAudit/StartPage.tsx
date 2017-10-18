import * as React from 'react';

import corlaDate from 'corla/date';

import Nav from '../Nav';

import ElectionDateForm from './ElectionDateForm';
import ElectionTypeForm from './ElectionTypeForm';
import PublicMeetingDateForm from './PublicMeetingDateForm';
import RiskLimitForm from './RiskLimitForm';

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

interface ReadOnlyRiskLimitProps {
    riskLimit: number;
}

const ReadonlyRiskLimit = ({ riskLimit }: ReadOnlyRiskLimitProps) => {
    const riskLimitPercent = round(riskLimit * 100, 2);

    return (
        <div className='pt-card'>
            <h4>Risk limit set.</h4>
            <div>The risk limit is set at: { riskLimitPercent }%</div>
        </div>
    );
};

interface NextButtonProps {
    nextPage: OnClick;
}

const NextButton = (props: NextButtonProps) => {
    const { nextPage } = props;

    return (
        <button onClick={ nextPage } className='pt-button pt-intent-primary'>
            Next
        </button>
    );
};

interface SaveButtonProps {
    disabled: boolean;
    forms: any;
}

const SaveButton = (props: SaveButtonProps) => {
    const { disabled, forms } = props;

    const buttonClick = () => {
        const electionDate = corlaDate.parse(forms.electionDateForm.date);
        const { type } = forms.electionTypeForm;
        const publicMeetingDate = corlaDate.parse(forms.publicMeetingDateForm.date);
        const riskLimit = forms.riskLimit.comparisonLimit;

        setAuditInfo({
            election: {
                date: electionDate,
                type,
            },
            publicMeetingDate,
            riskLimit,
        });
    };

    return (
        <button
            disabled={ disabled }
            onClick={ buttonClick }
            className='pt-button pt-intent-primary'>
            Save
        </button>
    );
};

interface ReadOnlyPageProps {
    election: Election;
    nextPage: OnClick;
    publicMeetingDate: Date;
    riskLimit: number;
}

const ReadOnlyPage = (props: ReadOnlyPageProps) => {
    const { election, nextPage, riskLimit } = props;

    const electionDate = corlaDate.format(election.date);
    const electionType = format.electionType(election.type);
    const publicMeetingDate = corlaDate.format(props.publicMeetingDate);

    return (
        <div>
            <Nav />
            <Breadcrumb />

            <h2>Administer an Audit</h2>

            <div className='pt-card'>
                <h3>Election Info</h3>
                <div className='pt-card'>
                    <div>Election Date: { electionDate }</div>
                    <div>Election Type: { electionType }</div>
                    <div>Public Meeting Date: { publicMeetingDate }</div>
                </div>
            </div>

            <div className='pt-card'>
                <h3>Risk Limit</h3>
                <ReadonlyRiskLimit riskLimit={ riskLimit } />
            </div>
            <NextButton nextPage={ nextPage } />
        </div>
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

    if (electionAndRiskLimitSet) {
        return (
            <ReadOnlyPage
                election={ election }
                nextPage={ nextPage }
                publicMeetingDate={ publicMeetingDate }
                riskLimit={ riskLimit } />
        );
    }

    const forms = {};

    const disableButton = !formValid;

    return (
        <div>
            <Nav />
            <Breadcrumb />

            <h2>Administer an Audit</h2>

            <div className='pt-card'>
                <h3>Election Info</h3>
                <div>Enter the date the election will take place, and the type of election.</div>
                <ElectionDateForm forms={ forms } />
                <ElectionTypeForm forms={ forms } setFormValid={ setFormValid } />
            </div>

            <div className='pt-card'>
                <h3>Public Meeting Date</h3>
                <div>Enter the date of the public meeting to establish the random seed.</div>
                <PublicMeetingDateForm forms={ forms } />
            </div>

            <div className='pt-card'>
                <h3>Risk Limit</h3>
                <div>
                    Enter the risk limit for comparison audits as a percentage.
                </div>
                <RiskLimitForm forms={ forms }
                               riskLimit={ riskLimit }
                               setFormValid={ setFormValid } />
                <div className='pt-card'>
                    <span className='pt-icon pt-intent-warning pt-icon-warning-sign' />
                    <span> </span>
                    Once saved, this risk limit cannot be modified.
                </div>
                <SaveButton
                    disabled={ disableButton }
                    forms={ forms } />
            </div>
        </div>
    );
};


export default AuditPage;
