import * as React from 'react';


const Status = (props: any) => {
    const { currentRound, finishedCountiesCount, totalCountiesCount } = props;

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
