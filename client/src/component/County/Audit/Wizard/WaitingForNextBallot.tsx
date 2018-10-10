import * as React from 'react';

import { Intent, Spinner } from '@blueprintjs/core';


const WaitingForNextBallot = () => {
    return (
        <div className='rla-page'>
            <h2>Ballot Card Verification</h2>
            <div className='pt-card'>
                <div className='pt-card'>
                    Loading next ballot...
                </div>
                <Spinner className='pt-large' intent={ Intent.PRIMARY } />
            </div>
            <button className='pt-button pt-intent-primary pt-breadcrumb' disabled>
                Back
            </button>
            <button className='pt-button pt-intent-primary pt-breadcrumb' disabled>
                Review
            </button>
        </div>
    );
};

export default WaitingForNextBallot;
