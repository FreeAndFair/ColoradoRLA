import * as React from 'react';

import * as _ from 'lodash';

import SoSNav from './Nav';


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

const CountyUpdates = ({ counties }: any) => {
    const countyStatuses = _.map(counties, (c: any) => (
        <tr key={ c.id }>
            <td>{ c.id }</td>
            <td>{ c.name }</td>
            <td>{ c.started }</td>
            <td>{ c.submitted }</td>
            <td>{ c.progress }</td>
        </tr>
    ));

    return (
        <div className='pt-card'>
            <h3>County Updates</h3>
            <div className='pt-card'>
                <table className='pt-table'>
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Started</td>
                            <td>Submitted</td>
                            <td>Progress</td>
                        </tr>
                    </thead>
                    <tbody>
                        { ...countyStatuses }
                    </tbody>
                </table>
            </div>
        </div>
    );
};


const SoSHomePage = (props: any) => {
    const { contests, counties, dosDashboardRefresh, seed } = props;

    setTimeout(dosDashboardRefresh, 1000);

    return (
        <div className='sos-home'>
            <SoSNav />
            <div className='sos-notifications pt-card'>
                <em>No notifications.</em>
            </div>
            <div className='sos-info pt-card'>
                <CountyUpdates counties={ counties} />
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
