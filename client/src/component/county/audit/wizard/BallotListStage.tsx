import * as React from 'react';


const BallotListStage = (props: any) => {
    const { county, countyInfo, cvrsToAudit, nextStage } = props;

    const ballotListRows = cvrsToAudit.map((cvr: any) => {
        return (
            <tr key={ cvr.imprinted_id }>
                <td>{ cvr.scanner_id }</td>
                <td>{ cvr.batch_id }</td>
                <td>{ cvr.record_id }</td>
                <td>{ cvr.storage_location }</td>
            </tr>
        );
    });

    const contestsUnderAuditListItems = county.contestsUnderAudit.map((c: any) => {
        return (
            <li key={ c.id }>
                { c.name } – { county.riskLimit }
            </li>
        );
    });

    return (
        <div>
            <h3>Ballots to audit</h3>
            <div className='pt-card'>
                <div>
                    The Secretary of State has established the following risk limit(s) for
                    the following ballot contest(s) to audit:
                </div>
                <ul>
                    { contestsUnderAuditListItems }
                </ul>
            </div>
            <div className='pt-card'>
                The Secretary of State has randomly selected [total number of ballots]
                for the { countyInfo.name } County Audit Board to examine to satisfy the
                risk limit(s) for the audited contest(s).
            </div>
            <div className='pt-card'>
                The Audit Board must locate and retrieve, or observe a county staff member
                locate and retrieve, the following randomly selected ballots for the initial
                round of this risk-limiting audit:
            </div>
            <table className='pt-table pt-bordered pt-condensed'>
                <thead>
                    <tr>
                        <th>Scanner #</th>
                        <th>Batch #</th>
                        <th>Ballot Position #</th>
                        <th>Storage Bin</th>
                    </tr>
                </thead>
                <tbody>
                    { ballotListRows }
                </tbody>
            </table>
            <div className='pt-card'>
                Audit Board: Click Next to start reporting the votes you observe on each
                of the above ballots.
            </div>
            <button
                className='pt-button pt-intent-primary'
                onClick={ nextStage }>
                Next
            </button>
        </div>
    );
};


export default BallotListStage;
