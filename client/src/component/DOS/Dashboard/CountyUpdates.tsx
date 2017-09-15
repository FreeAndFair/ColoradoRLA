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

type SortKey = 'name'
             | 'status'
             | 'submitted'
             | 'auditedDisc'
             | 'oppDisc'
             | 'disagreements'
             | 'remRound'
             | 'remTotal';

function sortIndex(sort: SortKey): number {
    // tslint:disable
    const index: any = {
        name: 1,
        status: 2,
        submitted: 3,
        auditedDisc: 4,
        oppDisc: 5,
        disagreements: 6,
        remRound: 7,
        remTotal: 8,
    };
    // tslint:enable

    return index[sort];
}

type SortOrder = 'asc' | 'desc';

class CountyUpdates extends React.Component<any, any> {
    public state: any = {
        order: 'asc',
        sort: 'name',
    };

    public render() {
        const { countyStatus } = this.props;

        const countyData = _.map(countyStatus, (c: any) => {
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

            return [
                c.id,
                county.name,
                status,
                c.auditedBallotCount,
                auditedDiscrepancyCount,
                opportunisticDisrepancyCount,
                disagreementCount,
                c.ballotsRemainingInRound,
                Math.max(0, c.estimatedBallotsToAudit),
            ];
        });

        const keyFunc = (d: any[]) => d[sortIndex(this.state.sort)];
        const sortedCountyData = _.sortBy(countyData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedCountyData);
        }

        const countyStatusRows = _.map(sortedCountyData, (x: any) => {
            return (
                <tr key={ x[0] }>
                    <td>{ x[1] }</td>
                    <td>{ x[2] }</td>
                    <td>{ x[3] }</td>
                    <td>{ x[4] }</td>
                    <td>{ x[5] }</td>
                    <td>{ x[6] }</td>
                    <td>{ x[7] }</td>
                    <td>{ x[8] }</td>
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
                                <th onClick={ this.sortBy('name') }>
                                    Name
                                </th>
                                <th onClick={ this.sortBy('status') }>
                                    Status
                                </th>
                                <th onClick={ this.sortBy('submitted') }>
                                    Submitted
                                </th>
                                <th onClick={ this.sortBy('auditedDisc') }>
                                    Audited Contest Discrepancies
                                </th>
                                <th onClick={ this.sortBy('oppDisc') }>
                                    Non-audited Contest Discrepancies
                                </th>
                                <th onClick={ this.sortBy('disagreements') }>
                                    Disagreements
                                </th>
                                <th onClick={ this.sortBy('remRound') }>
                                    <RemainingInRoundHeader />
                                </th>
                                <th onClick={ this.sortBy('remTotal') }>
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

    private sortBy(sort: SortKey) {
        return () => {
            if (this.state.sort === sort) {
                this.reverseOrder();
            } else {
                const order = 'asc';
                this.setState({ sort, order });
            }
        };
    }

    private reverseOrder() {
        const order = this.state.order === 'asc'
                    ? 'desc'
                    : 'asc';

        this.setState({ order });
    }
}


export default CountyUpdates;
