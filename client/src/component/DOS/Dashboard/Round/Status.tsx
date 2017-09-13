import * as React from 'react';


const Status = (props: any) => {
    const { activeCounties, currentRound } = props;

    const totalCountiesCount = activeCounties.length;

    const finished = (c: any) => c.auditBoardAsmState === 'WAITING_FOR_ROUND_START';
    const finishedCountiesCount = activeCounties.filter(finished).length;

    return (
        <div className='pt-card'>
            <h4>Round status</h4>
            <div>
                Round { currentRound } in progress.
            </div>
            <div>
                { finishedCountiesCount } of { totalCountiesCount } Counties
                have finished this round.
            </div>
        </div>
    );
};


export default Status;
