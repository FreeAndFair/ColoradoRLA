import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';


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

const ContestUpdates = ({ contests, seed, sos }: any) => {
    const contestStatuses = _.map(contests, (c: any) => {
        if (!sos.auditedContests) {
            return <tr key={ c.id }><td /><td /><td /><td /><td /></tr>;
        }

        const status = sos.auditedContests[c.id]
                     ? 'Under audit'
                     : 'Not selected for audit';

        const riskLimitPercent = sos.riskLimit * 100;

        return (
            <tr key={ c.id}>
                <td>{ c.id }</td>
                <td>{ c.name }</td>
                <td>{ status }</td>
                <td>{ riskLimitPercent }%</td>
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
                            <td>ID</td>
                            <td>Name</td>
                            <td>Audit Status</td>
                            <td>Target Risk Limit</td>
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
