import * as React from 'react';


const Status = (props: any) => {
    const { activeCounties, currentRound } = props;

    return (
        <div className='pt-card'>
            <h4>Round status</h4>
            <div>
                Current round: #{ currentRound }
            </div>
        </div>
    );
};


export default Status;
