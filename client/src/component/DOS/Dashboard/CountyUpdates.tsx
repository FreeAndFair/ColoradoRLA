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

class CountyUpdates extends React.Component<any, any> {
    public render() {
        const { countyStatus } = this.props;
        const countyStatusRows = _.map(countyStatus, (c: any) => {
            const county = _.find(counties, (x: any) => x.id === c.id);

            const status = formatCountyAsmState(c.asmState);
            const auditedDiscrepancyCount = c.discrepancyCount
                                          ? c.discrepancyCount.audited
                                          : '—';
            const opportunisticDisrepancyCount = c.discrepancyCount
                                               ? c.discrepancyCount.opportunistic
                                               : '—';

            const disagreementCount = _.isNil(c.disagreementCount)
                                    ? '—'
                                    : c.disagreementCount;

            return (
                <tr key={ c.id }>
                    <td>{ county.name }</td>
                    <td>{ status }</td>
                    <td>{ c.auditedBallotCount }</td>
                    <td>{ auditedDiscrepancyCount }</td>
                    <td>{ opportunisticDisrepancyCount }</td>
                    <td>{ disagreementCount }</td>
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
                                <th>Name</th>
                                <th>Status</th>
                                <th>Submitted</th>
                                <th>Audited Contest Discrepancies</th>
                                <th>Non-audited Contest Discrepancies</th>
                                <th>Disagreements</th>
                                <th>
                                    <RemainingInRoundHeader />
                                </th>
                                <th>
                                    <EstRemainingHeader />
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            { ...countyStatusRows }
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
};


export default CountyUpdates;
