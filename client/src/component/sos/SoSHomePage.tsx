import * as React from 'react';

import SoSNav from './Nav';


const AuditRounds = () => {
    return (
        <div>
            List of Audit Rounds (number of ballots, status by County, download links)
        </div>
    );
};

const SeedInfo = () => {
    return (
        <div>
        </div>
    );
};

const ContestUpdates = () => {
    return (
        <div className='pt-card'>
            <h3>Contest updates.</h3>
            <div>
                Target and Current Risk Limits by Contest
            </div>
            <div>
                Status (audit required, audit in progress, audit complete, hand
                count required, hand count complete) by Audited Contest
            </div>
            <div>
                Seed: 23781234
            </div>
            <AuditRounds />
        </div>
    );
};

const CountyUpdates = () => {
    return (
        <div className='pt-card'>
            <h3>County Updates</h3>
            <div>
                County Ballot Manifests, CVRs and Hashes (status & download links)
            </div>
            <div>
                [Ballot Order]
                [For each audited contest, total number of ballots to be audited
                (including previous rounds)]
            </div>
            <div>
                Updates based on county progress, discrepencies.
            </div>
        </div>
    );
};


const SoSHomePage = (props: any) => {
    return (
        <div className='sos-home'>
            <SoSNav />
            <div className='sos-notifications pt-card'>
                <em>No notifications.</em>
            </div>
            <div className='sos-info pt-card'>
                <CountyUpdates />
                <ContestUpdates />
            </div>
            <div>
                <button disabled className='pt-button pt-intent-primary'>
                    Final Audit Report
                </button>
            </div>
        </div>
    );
};


export default SoSHomePage;
