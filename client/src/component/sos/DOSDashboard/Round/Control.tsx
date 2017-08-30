import * as React from 'react';

import startNextRound from '../../../../action/dosStartNextRound';


const Control = (props: any) => {
    const { currentRound } = props;

    return (
        <div className='pt-card'>
            <h4>Start next round</h4>
            <div className='pt-card'>
                Round #{ currentRound + 1 } has completed.
                Start round #{ currentRound + 2 }?
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
