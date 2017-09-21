import * as React from 'react';


const Status = (props: any) => {
    const { countiesWithRound, currentRound } = props;

    const totalCountiesCount = countiesWithRound.length;

    const finished = (c: any) => c.currentRound.number !== currentRound;
    const finishedCountiesCount = countiesWithRound.filter(finished).length;

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
