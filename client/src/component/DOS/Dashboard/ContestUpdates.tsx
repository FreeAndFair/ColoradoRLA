import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

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

const HandCountButton = (props: any) => {
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

function sortIndex(sort: SortKey): number {
    // tslint:disable
    const index: any = {
        county: 0,
        name: 1,
        discrepancyCount: 2,
    };
    // tslint:enable

    return index[sort];
}

class ContestUpdates extends React.Component<any, any> {
    public state: any = {
        order: 'asc',
        sort: 'name',
    };

    public render() {
        const { contests, seed, sos } = this.props;

        const selectedContests = _.values(_.pick(contests, _.keys(sos.auditedContests)));

        const rowData = _.map(selectedContests, (c: any) => {
            const county = counties[c.countyId];
            const discrepancyCount = _.sum(_.values(sos.discrepancyCounts[c.id]));

            return [county.name, c.name, discrepancyCount, c];
        });

        const keyFunc = (d: any[]) => d[sortIndex(this.state.sort)];
        const sortedData = _.sortBy(rowData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedData);
        }

        const contestStatuses = _.map(sortedData, (row: any) => {
            const [countyName, contestName, discrepancyCount, c] = row;

            if (!sos.auditedContests) {
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

        return (
            <div className='pt-card'>
                <h3>Contest Updates</h3>
                <div className='pt-card'>
                    <table className='pt-table'>
                        <thead>
                            <tr>
                                <th>Hand Count</th>
                                <th onClick={ this.sortBy('county') }>
                                    County
                                </th>
                                <th onClick={ this.sortBy('name') }>
                                    Name
                                </th>
                                <th onClick={ this.sortBy('discrepancyCount') }>
                                    Discrepancies
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
