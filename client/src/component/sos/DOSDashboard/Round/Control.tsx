import * as React from 'react';

import startNextRound from '../../../../action/dosStartNextRound';


const Control = (props: any) => {
    return (
        <div className='pt-card'>
            <h4>Start next round</h4>
            <div className='pt-card'>
                Round [Round #N] has completed.
                Start round [Round #N+1]?
            </div>
            <button
                className='pt-button pt-intent-primary'
                onClick={ startNextRound }>
                Start Round
            </button>
        </div>
    );
};


export default Control;
