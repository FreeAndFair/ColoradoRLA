import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

import counties from 'corla/data/counties';

import { formatCountyAsmState } from 'corla/format';


const RemainingInRoundHeader = () => {
    const content =
        'Number of ballots remaining to audit in the current round.';

    return (
        <Tooltip
            className='pt-tooltip-indicator'
            content={ content }>
            <div>
                <span>Remaining in Round</span>
                <span> </span>
                <span className='pt-icon-standard pt-icon-help' />
            </div>
        </Tooltip>
    );
};

const EstRemainingHeader = () => {
    const content =
        'Estimated number of ballots remaining to audit to meet risk limit.';

    return (
        <Tooltip
            className='pt-tooltip-indicator'
            content={ content }>
            <div>
                <span>Est. Remaining Ballots</span>
                <span> </span>
                <span className='pt-icon-standard pt-icon-help' />
            </div>
        </Tooltip>
    );
};

const CountyUpdates = ({ countyStatus }: any) => {
    const countyStatusRows = _.map(countyStatus, (c: any) => {
        const county = _.find(counties, (x: any) => x.id === c.id);

        const status = formatCountyAsmState(c.asmState);

        return (
            <tr key={ c.id }>
                <td>{ county.name }</td>
                <td>{ status }</td>
                <td>{ c.auditedBallotCount }</td>
                <td>{ c.discrepancyCount }</td>
                <td>{ c.disagreementCount }</td>
                <td>{ c.ballotsRemainingInRound }</td>
                <td>{ Math.max(0, c.estimatedBallotsToAudit) }</td>
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
                            <td>Name</td>
                            <td>Status</td>
                            <td>Submitted</td>
                            <td>Discrepancies</td>
                            <td>Disagreements</td>
                            <td>
                                <RemainingInRoundHeader />
                            </td>
                            <td>
                                <EstRemainingHeader />
                            </td>
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
