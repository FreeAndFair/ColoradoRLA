import * as React from 'react';

import * as _ from 'lodash';

import SoSNav from './Nav';

import counties from '../../data/counties';


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
            <td>{ c.riskLimit }</td>
            <td>{ c.riskLevel }</td>
        </tr>
    ));

    return (
        <div className='pt-card'>
            <h3>Contest Updates</h3>
            <div className='pt-card'>
                <table className='pt-table'>
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Status</td>
                            <td>Target Risk Limit</td>
                            <td>Risk Level</td>
                        </tr>
                    </thead>
                    <tbody>
                        { ...contestStatuses }
                    </tbody>
                </table>
            </div>
            <SeedInfo seed={ seed } />
        </div>
    );
};

const CountyUpdates = ({ countyStatus }: any) => {
    const countyStatusRows = _.map(countyStatus, (c: any) => {
        const county = _.find(counties, (x: any) => x.id === c.id);
        const started = c.auditedBallotCount ? '✓' : '';

        return (
            <tr key={ c.id }>
                <td>{ c.id }</td>
                <td>{ county.name }</td>
                <td>{ started }</td>
                <td>{ c.auditedBallotCount }</td>
                <td>{ c.discrepancyCount }</td>
                <td>{ c.disagreementCount }</td>
                <td>{ c.estimatedBallotsToAudit }</td>
            </tr>
        );
    });

    return (
        <div className='pt-card'>
            <h3>County Updates</h3>
            <div className='pt-card'>
                <table className='pt-table pt-bordered pt-condensed '>
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Started</td>
                            <td>Submitted</td>
                            <td>Discrepancies</td>
                            <td>Disagreements</td>
                            <td>To Audit</td>
                        </tr>
                    </thead>
                    <tbody>
                        { ...countyStatusRows }
                    </tbody>
                </table>
            </div>
        </div>
    );
};


const SoSHomePage = (props: any) => {
    const { contests, countyStatus, seed } = props;

    return (
        <div className='sos-home'>
            <SoSNav />
            <div className='sos-notifications pt-card'>
                <em>No notifications.</em>
            </div>
            <div className='sos-info pt-card'>
                <CountyUpdates countyStatus={ countyStatus } />
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
