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

type SortKey = 'county'
             | 'name'
             | 'discrepancyCount';

type SortOrder = 'asc' | 'desc';

function sortIndex(sort: SortKey): number {
    // tslint:disable
    const index = {
        county: 0,
        name: 1,
        discrepancyCount: 2,
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

        type RowData = [string, string, number, Contest];

        const rowData: RowData[] = _.map(selectedContests, (c): RowData => {
            const county: CountyInfo = counties[c.countyId];
            const discrepancyCount: number = _.sum(_.values(dosState.discrepancyCounts![c.id]));

            return [county.name, c.name, discrepancyCount, c];
        });

        const keyFunc = (d: RowData) => d[sortIndex(this.state.sort)];
        const sortedData = _.sortBy(rowData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedData);
        }

        const filterName = (d: RowData) => {
            const countyName = d[0].toLowerCase();
            const contestName = d[1].toLowerCase();
            const str = this.state.filter.toLowerCase();

            return countyName.includes(str) || contestName.includes(str);
        };
        const filteredData = _.filter(sortedData, filterName);

        const contestStatuses = _.map(filteredData, row => {
            const [countyName, contestName, discrepancyCount, c] = row;

            if (!dosState.auditedContests) {
                return <tr key={ c.id }><td /><td /><td /><td /><td /></tr>;
            }

            return (
                <tr key={ c.id }>
                    <td>
                        <HandCountButton contest={ c } />
                    </td>
                    <td>{ countyName }</td>
                    <td>{ contestName }</td>
                    <td>{ discrepancyCount }</td>
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
                                <th onClick={ this.sortBy('county') }>
                                    County
                                    <span> </span>
                                    { sortIconForCol('county') }
                                </th>
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
