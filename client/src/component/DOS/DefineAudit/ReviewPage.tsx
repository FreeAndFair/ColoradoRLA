import * as React from 'react';

import * as _ from 'lodash';

import Nav from '../Nav';

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
                Review
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

const SelectedContests = (props: SelectedContestsProps) => {
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

interface AuditReviewProps {
    back: OnClick;
    publishBallotsToAudit: OnClick;
    saveAndDone: OnClick;
    dosState: DOS.AppState;
}

const AuditReview = (props: AuditReviewProps) => {
    const { back, publishBallotsToAudit, saveAndDone, dosState } = props;

    const launch = () => {
        publishBallotsToAudit();
        saveAndDone();
    };

    const riskLimitPercent = dosState.riskLimit! * 100;

    const disableLaunchButton = !dosState.seed;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Audit</h2>
            <h3>Audit Definition Review</h3>
            <div>
                This is the set of audit data which will be used to define the list of
                ballots to audit for each county. Once this is submitted, it will be released
                to the counties and the previous pages will not be editable.
                In particular, you will not be able to change which contests are under audit.
            </div>
            <div className='pt-card'>
                <table className='pt-table'>
                    <tbody>
                        <tr>
                            <td>Risk Limit:</td>
                            <td>{ riskLimitPercent }%</td>
                        </tr>
                        <tr>
                            <td>Random Number Generator Seed:</td>
                            <td>{ dosState.seed }</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <SelectedContests auditedContests={dosState.auditedContests}
                              contests={dosState.contests} />
            <div>
                <button onClick={ back } className='pt-button'>
                    Back
                </button>
                <button disabled={ disableLaunchButton }
                        onClick={ launch }
                        className='pt-button pt-intent-primary'>
                    Launch Audit
                </button>
            </div>
        </div>
    );
};


export default AuditReview;
