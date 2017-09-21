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

const ContestUpdates = ({ contests, seed, sos }: any) => {
    const selectedContests = _.pick(contests, _.keys(sos.auditedContests));

    const contestStatuses = _.map(selectedContests, (c: any) => {
        if (!sos.auditedContests) {
            return <tr key={ c.id }><td /><td /><td /><td /><td /></tr>;
        }

        const county = counties[c.countyId];
        const discrepancyCount = _.sum(_.values(sos.discrepancyCounts[c.id]));

        return (
            <tr key={ c.id }>
                <td>
                    <HandCountButton contest={ c } />
                </td>
                <td>{ county.name }</td>
                <td>{ c.name }</td>
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
                            <th>County</th>
                            <th>Name</th>
                            <th>Discrepancies</th>
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


export default ContestUpdates;
