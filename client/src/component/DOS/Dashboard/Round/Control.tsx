import * as React from 'react';

import startNextRound from 'corla/action/dos/startNextRound';


interface  ControlProps {
    canStartNextRound: boolean;
    currentRound: number;
}

const Control = (props: ControlProps) => {
    const { canStartNextRound, currentRound } = props;

    const buttonDisabled = !canStartNextRound;

    return (
        <div className='pt-card'>
            <h4>Start next round</h4>
            <div className='pt-card'>
                Round { currentRound } completed.
            </div>
            <div className='pt-card'>
                <div>
                    Start Round { currentRound + 1 }?
                </div>
                <div>
                    <button
                        className='pt-button pt-intent-primary'
                        disabled={ buttonDisabled }
                        onClick={ startNextRound }>
                        Start Round
                    </button>
                </div>
            </div>
        </div>
    );
};


export default Control;
