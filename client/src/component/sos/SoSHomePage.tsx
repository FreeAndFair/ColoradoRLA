import * as React from 'react';

import * as _ from 'lodash';

import SoSNav from './Nav';


const AuditRounds = () => {
    return (
        <div>
            List of Audit Rounds (number of ballots, status by County, download links)
        </div>
    );
};

const SeedInfo = ({ seed }: any) => {
    return (
        <div className='pt-card'>
            <strong>Seed: </strong> { seed }
        </div>
    );
};

const ContestUpdates = ({ contests, seed }: any) => {
    const contestStatuses = _.map(contests, (c: any) => (
        <tr key={ c.id}>
            <td>{ c.id }</td>
            <td>{ c.name }</td>
            <td>{ c.status }</td>
        </tr>
    ));

    return (
        <div className='pt-card'>
            <h3>Contest updates.</h3>
            <div>
                Target and Current Risk Limits by Contest
            </div>
            <div className='pt-card'>
                <table className='pt-table'>
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Status</td>
                        </tr>
                    </thead>
                    <tbody>
                        { ...contestStatuses }
                    </tbody>
                </table>
            </div>
            <SeedInfo seed={ seed } />
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
    const { contests, seed } = props;
    console.log(contests);
    return (
        <div className='sos-home'>
            <SoSNav />
            <div className='sos-notifications pt-card'>
                <em>No notifications.</em>
            </div>
            <div className='sos-info pt-card'>
                <CountyUpdates />
                <ContestUpdates contests={ contests } seed={ seed } />
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
