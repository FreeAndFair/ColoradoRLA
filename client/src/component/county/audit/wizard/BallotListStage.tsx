import * as React from 'react';


const BallotListStage = (props: any) => {
    return (
        <div>
            <h3>Ballots to audit</h3>
            <div className='pt-card'>
                The Secretary of State has established the following risk limit(s) for
                the following ballot contest(s) to audit: [Contest Name] – [Risk Limit]
            </div>
            <div className='pt-card'>
                The Secretary of State has randomly selected [total number of ballots]
                for the [County name] County Audit Board to examine to satisfy the risk
                limit(s) for the audited contest(s).
            </div>
            <div className='pt-card'>
                The Audit Board must locate and retrieve, or observe a county staff member
                locate and retrieve, the following randomly selected ballots for the initial
                round of this risk-limiting audit:
            </div>
        </div>
    );
}


export default BallotListStage;
