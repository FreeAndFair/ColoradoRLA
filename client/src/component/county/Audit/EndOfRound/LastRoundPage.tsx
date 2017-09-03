import * as React from 'react';

import CountyNav from 'corla/component/county/Nav';


const EndOfRoundPage = (props: any) => {
    const { countyInfo, previousRound, previousRoundSignedOff } = props;

    const countyName = countyInfo.name;
    const roundNumber = previousRound.number;

    return (
        <div>
            <CountyNav />
            <h3>End of All Audit Rounds</h3>
            <div className='pt-card'>
                Congratulations! You have completed reporting the votes on all ballots
                for all rounds of the risk-limiting audit of the { countyName } County
                [Election Date] [Election Type] Election.
            </div>
        </div>
    );
};


export default EndOfRoundPage;
