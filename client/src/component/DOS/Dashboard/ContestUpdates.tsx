import * as React from 'react';

import * as _ from 'lodash';

import { EditableText, Tooltip } from '@blueprintjs/core';

import counties from 'corla/data/counties';

import setHandCount from 'corla/action/dos/setHandCount';


const RemainingToAuditHeader = () => {
    const content =
        'Estimated number of ballots to audit to meet risk limit.';

    return (
        <Tooltip
            className='pt-tooltip-indicator'
            content={ content }>
            <div>
                <span>Remaining to Audit </span>
                <span className='pt-icon-standard pt-icon-help' />
            </div>
        </Tooltip>
    );
};

interface ButtonProps {
    contest: Contest;
}

const HandCountButton = (props: ButtonProps) => {
    const { contest } = props;

    const onClick = () => setHandCount(contest.id);

    return (
        <button className='pt-button pt-intent-primary' onClick={ onClick }>
            <span className='pt-icon pt-icon-hand-up' />
        </button>
    );
};

type SortKey = 'name'
             | 'discrepancyCount'
             | 'estimatedBallotsToAudit';

type SortOrder = 'asc' | 'desc';

function sortIndex(sort: SortKey): number {
    // tslint:disable
    const index = {
        name: 1,
        discrepancyCount: 2,
        estimatedBallotsToAudit: 3,

    };
    // tslint:enable

    return index[sort];
}

interface UpdatesProps {
    contests: DOS.Contests;
    seed: string;
    dosState: DOS.AppState;
}

interface UpdatesState {
    filter: string;
    order: SortOrder;
    sort: SortKey;
}

class ContestUpdates extends React.Component<UpdatesProps, UpdatesState> {
    public state: UpdatesState = {
        filter: '',
        order: 'asc',
        sort: 'name',
    };

    public render() {
        const { contests, dosState, seed } = this.props;

        const selectedContests: DOS.Contests =
            _.values(_.pick(contests, _.keys(dosState.auditedContests)));

        type RowData = [string, number, number, Contest];

        const rowData: RowData[] = _.map(selectedContests, (c): RowData => {
            const discrepancyCount: number = _.sum(_.values(dosState.discrepancyCounts![c.id]));
            const estimatedBallotsToAudit = dosState.estimatedBallotsToAudit![c.id];

            return [c.name, discrepancyCount, estimatedBallotsToAudit, c];
        });

        const keyFunc = (d: RowData) => d[sortIndex(this.state.sort)];
        const sortedData = _.sortBy(rowData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedData);
        }

        const filterName = (d: RowData) => {
            const contestName = d[0].toLowerCase();
            const str = this.state.filter.toLowerCase();

            return contestName.includes(str);
        };
        const filteredData = _.filter(sortedData, filterName);

        const contestStatuses = _.map(filteredData, row => {
            const [contestName, discrepancyCount, estimatedBallotsToAudit, c] = row;

            return (
                <tr key={ c.id }>
                    <td>
                        <HandCountButton contest={ c } />
                    </td>
                    <td>{ contestName }</td>
                    <td>{ discrepancyCount }</td>
                    <td>{ estimatedBallotsToAudit }</td>
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
                <h3>Contest Updates</h3>
                <div className='pt-card'>
                    <strong>Filter by County or Contest Name:</strong>
                    <span> </span>
                    <EditableText
                        className='pt-input'
                        minWidth={ 200 }
                        value={ this.state.filter }
                        onChange={ this.onFilterChange } />
                </div>
                <div className='pt-card'>
                    <table className='pt-table'>
                        <thead>
                            <tr>
                                <th>Hand Count</th>
                                <th onClick={ this.sortBy('name') }>
                                    Name
                                    <span> </span>
                                    { sortIconForCol('name') }
                                </th>
                                <th onClick={ this.sortBy('discrepancyCount') }>
                                    Discrepancies
                                    <span> </span>
                                    { sortIconForCol('discrepancyCount') }
                                </th>
                                <th onClick={ this.sortBy('estimatedBallotsToAudit') }>
                                    Est. Ballots to Audit
                                    <span> </span>
                                   { sortIconForCol('estimatedBallotsToAudit') }
                                </th>

                            </tr>
                        </thead>
                        <tbody>
                            { ...contestStatuses }
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


export default ContestUpdates;
