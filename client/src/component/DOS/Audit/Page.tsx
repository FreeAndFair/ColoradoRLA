import * as React from 'react';

import Nav from '../Nav';

import ElectionDateForm from './ElectionDateForm';
import ElectionTypeForm from './ElectionTypeForm';
import RiskLimitForm from './RiskLimitForm';

import setElectionInfo from 'corla/action/dos/setElectionInfo';
import setRiskLimit from 'corla/action/dos/setRiskLimit';


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

const ReadonlyRiskLimit = ({ riskLimit }: any) => {
    const riskLimitPercent = round(riskLimit * 100, 2);

    return (
        <div className='pt-card'>
            <h4>Risk limit set.</h4>
            <div>The risk limit is set at: { riskLimitPercent }%</div>
        </div>
    );
};

const NextButton = (props: any) => {
    const { nextPage } = props;

    return (
        <button onClick={ nextPage } className='pt-button pt-intent-primary'>
            Next
        </button>
    );
};

const SaveButton = (props: any) => {
    const { forms, riskLimit } = props;

    const buttonClick = () => {
        const { date } = forms.electionDateForm;
        const { type } = forms.electionTypeForm;

        if (date && type) {
            setElectionInfo(date, type);
        }

        if (!riskLimit) {
            const { comparisonLimit } = forms.riskLimit;

            setRiskLimit(comparisonLimit);
        }
    };

    return (
        <button onClick={ buttonClick } className='pt-button pt-intent-primary'>
            Save
        </button>
    );
};

const AuditPage = (props: any) => {
    const { election, nextPage, riskLimit } = props;

    const forms: any = {};

    const riskLimitForm = riskLimit
                        ? <ReadonlyRiskLimit riskLimit={ riskLimit } />
                        : <RiskLimitForm forms={ forms } riskLimit={ riskLimit } />;

    const pageButton = riskLimit
                     ? <NextButton nextPage={ nextPage } />
                     : <SaveButton forms={ forms} riskLimit={ riskLimit } />;

    return (
        <div>
            <Nav />
            <Breadcrumb />

            <h2>Administer an Audit</h2>

            <div className='pt-card'>
                <h3>Audit Definition</h3>
                <div>Enter the date the election will take place, and the type of election.</div>
                <ElectionDateForm forms={ forms } />
                <ElectionTypeForm forms={ forms } />
            </div>

            <div className='pt-card'>
                <h3>Risk Limit</h3>
                <div>
                    Enter the risk limit for comparison audits as a percentage.
                </div>
                { riskLimitForm }
                <div className='pt-card'>
                    <span className='pt-icon pt-intent-warning pt-icon-warning-sign' />
                    <span> </span>
                    Once entered, this risk limit cannot be modified.
                </div>
                { pageButton }
            </div>
        </div>
    );
};


export default AuditPage;
