import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

import SoSNav from '../Nav';

import CountyUpdates from './CountyUpdates';
import MainContainer from './MainContainer';

import counties from '../../../data/counties';


const ContestUpdates = ({ contests, seed, sos }: any) => {
    const contestStatuses = _.map(contests, (c: any) => {
        if (!sos.auditedContests) {
            return <tr key={ c.id }><td /><td /><td /><td /><td /></tr>;
        }

        if (!sos.estimatedBallotsToAudit) {
            return <tr key={ c.id }><td /><td /><td /><td /><td /></tr>;
        }

        const status = sos.auditedContests[c.id] ? 'Under audit' : 'Not selected for audit';
        const toAudit = sos.estimatedBallotsToAudit[c.id];

        return (
            <tr key={ c.id}>
                <td>{ c.id }</td>
                <td>{ c.name }</td>
                <td>{ status }</td>
                <td>{ sos.riskLimit }</td>
                <td>{ toAudit || '—' }</td>
            </tr>
        );
    });

    const remainingToAuditTooltipContent =
        'Estimated number of ballots to audit to meet risk limit.';

    return (
        <div className='pt-card'>
            <h3>Contest Updates</h3>
            <div className='pt-card'>
                <table className='pt-table'>
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Name</td>
                            <td>Audit Status</td>
                            <td>Target Risk Limit</td>
                            <td>
                                <Tooltip
                                    className='pt-tooltip-indicator'
                                    content={ remainingToAuditTooltipContent }>
                                    <div>
                                        <span>Remaining to Audit </span>
                                        <span className='pt-icon-standard pt-icon-help' />
                                    </div>
                                </Tooltip>
                            </td>
                        </tr>
                    </thead>
                    <tbody>
                        { ...contestStatuses }
                    </tbody>
                </table>
            </div>
        </div>
    );
};



const DOSDashboardPage = (props: any) => {
    const { contests, countyStatus, seed, sos } = props;

    return (
        <div className='sos-home'>
            <SoSNav />
            <MainContainer />
            <div className='sos-info pt-card'>
                <CountyUpdates countyStatus={ countyStatus } />
                <ContestUpdates contests={ contests } seed={ seed } sos={ sos } />
            </div>
            <div>
                <button disabled className='pt-button pt-intent-primary'>
                    Final Audit Report
                </button>
            </div>
        </div>
    );
};


export default DOSDashboardPage;
