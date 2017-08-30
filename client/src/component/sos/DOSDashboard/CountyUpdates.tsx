import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

import counties from '../../../data/counties';


function formatStatus(asmState: any) {
    switch (asmState) {
        case 'COUNTY_INITIAL_STATE':
            return 'Not started';
        case 'COUNTY_AUTHENTICATED':
            return 'Logged in';
        case 'AUDIT_BOARD_OK':
            return 'Audit board established';
        case 'BALLOT_MANIFEST_OK':
            return 'Ballot manifest uploaded';
        case 'CVRS_OK':
            return 'CVR export uploaded';
        case 'AUDIT_BOARD_AND_BALLOT_MANIFEST_OK':
            return 'Ballot manifest uploaded';
        case 'AUDIT_BOARD_AND_CVRS_OK':
            return 'CVR export uploaded';
        case 'BALLOT_MANIFEST_AND_CVRS_OK':
            return 'Ballot manifest and CVR export uploaded';
        case 'AUDIT_BOARD_BALLOT_MANIFEST_AND_CVRS_OK':
            return 'Ballot manifest and CVR export uploaded';
        case 'COUNTY_AUDIT_UNDERWAY':
            return 'Audit underway';
        case 'COUNTY_AUDIT_COMPLETE':
            return 'Audit complete';
        case 'DEADLINE_MISSED':
            return 'File upload deadline missed';
        default: return '';
    }
}

const CountyUpdates = ({ countyStatus }: any) => {
    const countyStatusRows = _.map(countyStatus, (c: any) => {
        const county = _.find(counties, (x: any) => x.id === c.id);

        const status = formatStatus(c.asmState);

        return (
            <tr key={ c.id }>
                <td>{ county.name }</td>
                <td>{ status }</td>
                <td>{ c.auditedBallotCount }</td>
                <td>{ c.discrepancyCount }</td>
                <td>{ c.disagreementCount }</td>
                <td>{ c.ballotsRemainingInRound }</td>
                <td>{ c.estimatedBallotsToAudit }</td>
            </tr>
        );
    });

    const remainingInRoundTooltipContent =
        'Number of ballots remaining to audit in the current round.';

    const remainingInRoundHeader = (
        <Tooltip
            className='pt-tooltip-indicator'
            content={ remainingInRoundTooltipContent }>
            <div>
                <span>Remaining in Round</span>
                <span className='pt-icon-standard pt-icon-help' />
            </div>
        </Tooltip>
    );

    const estRemainingTooltipContent =
        'Estimated number of ballots remaining to audit to meet risk limit.';

    const estRemainingHeader = (
        <Tooltip
            className='pt-tooltip-indicator'
            content={ estRemainingTooltipContent }>
            <div>
                <span>Est. Remaining Ballots</span>
                <span className='pt-icon-standard pt-icon-help' />
            </div>
        </Tooltip>
    );

    return (
        <div className='pt-card'>
            <h3>County Updates</h3>
            <div className='pt-card'>
                <table className='pt-table pt-bordered pt-condensed '>
                    <thead>
                        <tr>
                            <td>Name</td>
                            <td>Status</td>
                            <td>Submitted</td>
                            <td>Discrepancies</td>
                            <td>Disagreements</td>
                            <td>{ remainingInRoundHeader }</td>
                            <td>{ estRemainingHeader }</td>
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


export default CountyUpdates;
