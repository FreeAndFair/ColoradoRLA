import * as React from 'react';

import * as _ from 'lodash';

import { EditableText, Tooltip } from '@blueprintjs/core';

import counties from 'corla/data/counties';

import { formatCountyAndBoardASMState } from 'corla/format';


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
    const index = {
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

interface UpdatesProps {
    auditStarted: boolean;
    countyStatus: DOS.CountyStatuses;
}

interface UpdatesState {
    filter: string;
    order: SortOrder;
    sort: SortKey;
}

class CountyUpdates extends React.Component<UpdatesProps, UpdatesState> {
    public state: UpdatesState = {
        filter: '',
        order: 'asc',
        sort: 'name',
    };

    public render() {
        const { auditStarted, countyStatus } = this.props;

        type RowData = [
            number,
            string,
            string,
            (number | string),
            (number | string),
            (number | string),
            (number | string),
            (number | string),
            (number | string)
        ];

        const countyData: RowData[] = _.map(countyStatus, (c): RowData => {
            const county = counties[c.id];
            const missedDeadline = c.asmState === 'DEADLINE_MISSED';
            const status = formatCountyAndBoardASMState(c.asmState, c.auditBoardASMState);

            if (!auditStarted) {
                return [c.id, county.name, status, '—', '—', '—', '—', '—', '—'];
            }

            if (auditStarted && missedDeadline) {
                return [c.id, county.name, status, '—', '—', '—', '—', '—', '—'];
            }

            const auditedDiscrepancyCount = c.discrepancyCount
                                          ? c.discrepancyCount.audited || 0
                                          : 0;
            const unauditedDiscrepancyCount = c.discrepancyCount
                                            ? c.discrepancyCount.unaudited || 0
                                            : 0;
            const disagreementCount = c.disagreementCount || 0;

            return [
                c.id,
                county.name,
                status,
                c.auditedBallotCount || 0,
                auditedDiscrepancyCount,
                unauditedDiscrepancyCount,
                disagreementCount,
                c.ballotsRemainingInRound,
                Math.max(0, c.estimatedBallotsToAudit),
            ];
        });

        const keyFunc = (d: RowData) => {
            const countyName = d[1];
            const sortVal = d[sortIndex(this.state.sort)];

            if (sortVal === '—') {
                // There are numeric and non-numeric columns. If the audit has not
                // started, all numeric columns will have the value '—', so it doesn't
                // matter what the _sort_ value is. If the audit has started, some
                // counties will have missed the file upload deadline. Their numeric
                // columns will display '—', but participating counties will have numeric
                // values. What we would like is for non-participating counties to appear
                // _after_ participating counties when sorting by a numeric column from
                // greatest to least. By treating '—' as -Infinity for sort purposes,
                // we guarantee it will be smaller than any participant numeric value.
                return [-Infinity, countyName];
            } else {
                return [sortVal, countyName];
            }
        };
        const sortedCountyData = _.sortBy(countyData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedCountyData);
        }

        const filterName = (d: RowData) => {
            const name = d[1].toLowerCase();
            const str = this.state.filter.toLowerCase();

            return name.includes(str);
        };
        const filteredCountyData = _.filter(sortedCountyData, filterName);

        const countyStatusRows = _.map(filteredCountyData, (x: RowData) => {
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

        const sortAscIcon = <span className='pt-icon-standard pt-icon-sort-asc' />;
        const sortDescIcon = <span className='pt-icon-standard pt-icon-sort-desc' />;

        const sortIconForCol = (col: string) => {
            if (col !== this.state.sort) {
                return null;
            }

            return this.state.order === 'asc'
                 ? sortAscIcon
                 : sortDescIcon;
        };

        return (
            <div className='pt-card'>
                <h3>County Updates</h3>
                <div className='pt-card'>
                    <strong>Filter by County Name:</strong>
                    <span> </span>
                    <EditableText
                        className='pt-input'
                        minWidth={ 200 }
                        value={ this.state.filter }
                        onChange={ this.onFilterChange } />
                </div>
                <div className='pt-card' >
                    <strong>Click on a column name to sort by that column's data.
                    To reverse sort, click on the column name again.</strong>
                </div>
                <div className='pt-card'>
                    <table className='pt-table pt-bordered pt-condensed '>
                        <thead>
                            <tr>
                                <th onClick={ this.sortBy('name') }>
                                    Name
                                    <span> </span>
                                    { sortIconForCol('name') }
                                </th>
                                <th onClick={ this.sortBy('status') }>
                                    Status
                                    <span> </span>
                                    { sortIconForCol('status') }
                                </th>
                                <th onClick={ this.sortBy('submitted') }>
                                    Submitted
                                    <span> </span>
                                    { sortIconForCol('submitted') }
                                </th>
                                <th onClick={ this.sortBy('auditedDisc') }>
                                    Audited Contest Discrepancies
                                    <span> </span>
                                    { sortIconForCol('auditedDisc') }
                                </th>
                                <th onClick={ this.sortBy('oppDisc') }>
                                    Non-audited Contest Discrepancies
                                    <span> </span>
                                    { sortIconForCol('oppDisc') }
                                </th>
                                <th onClick={ this.sortBy('disagreements') }>
                                    Disagreements
                                    <span> </span>
                                    { sortIconForCol('disagreements') }
                                </th>
                                <th onClick={ this.sortBy('remRound') }>
                                    <RemainingInRoundHeader />
                                    <span> </span>
                                    { sortIconForCol('remRound') }
                                </th>
                                <th onClick={ this.sortBy('remTotal') }>
                                    <EstRemainingHeader />
                                    <span> </span>
                                    { sortIconForCol('remTotal') }
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

    private onFilterChange = (filter: string) => {
        this.setState({ filter });
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
