import * as React from 'react';


interface StatusProps {
    currentRound: number;
    finishedCountiesCount: number;
    totalCountiesCount: number;
}

const Status = (props: StatusProps) => {
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
