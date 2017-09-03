import * as React from 'react';

import CountyNav from 'corla/component/County/Nav';

import SignOffFormContainer from './SignOffFormContainer';

import finishAudit from 'corla/action/county/finishAudit';


const PreviousRoundSignedOff = (props: any) => {
    const { roundNumber } = props;

    return (
        <div>
            <CountyNav />
            <h3> End of Round { roundNumber }</h3>
            <div className='pt-card'>
                The current round, Round #{ roundNumber } is complete.
                Please wait for the Department of State to begin the next round.
            </div>
        </div>
    );
};

const LastRoundComplete = (props: any) => {
    return (
        <div>
            <CountyNav />
            <h3> End of All Audit Rounds</h3>
            <div className='pt-card'>
                All audit rounds are complete. Please use the form below to
                certify that the county has completed the audit.
            </div>
            <div className='pt-card'>
                <button className='pt-button pt-intent-primary' onClick={ finishAudit }>
                    Submit
                </button>
            </div>
        </div>
    );
};

const EndOfRoundPage = (props: any) => {
    const {
        allRoundsComplete,
        countyInfo,
        estimatedBallotsToAudit,
        previousRound,
        previousRoundSignedOff,
    } = props;

    const countyName = countyInfo.name;
    const roundNumber = previousRound.number;

    if (allRoundsComplete && estimatedBallotsToAudit <= 0) {
        return <LastRoundComplete />;
    }

    if (previousRoundSignedOff) {
        return <PreviousRoundSignedOff roundNumber={ roundNumber } />;
    }

    return (
        <div>
            <CountyNav />
            <h3> End of Round { roundNumber }</h3>
            <div className='pt-card'>
                Congratulations! You have completed reporting the votes on all ballots
                randomly selected for this round of the risk-limiting audit of the
                <span> { countyName } </span> County [Election Date] [Election Type] Election.
            </div>

            <div className='pt-card'>
                Please complete this audit round by entering your names in the fields below,
                making the following certification, and selecting Submit. By entering his or
                her name and selecting Submit below, each Audit Board member individually
                certifies that he or she:

                <ul>
                    <li>
                        Personally located and retrieved, or personally observed a county staff
                        member locate and retrieve, each paper ballot randomly selected for
                        [audit round no.] of the <span> { countyName } </span> County [Election Date]
                        [Election Type] Election;
                    </li>
                    <li>
                        Personally examined each such randomly selected ballot;
                    </li>
                    <li>
                        Accurately entered the voter markings contained in each ballot contest
                        on each such randomly selected ballot, to the best of his or her ability;
                    </li>
                    <li>
                        Where applicable, resolved ambiguous markings, over votes and write-in
                        votes in accordance with the current version of the Secretary of State's
                        Voter Intent Guide;
                    </li>
                    <li>
                        In the case of physically duplicated ballots, if any, the Audit Board's
                        report reflects the voter markings on the paper ballot originally
                        submitted by the voter.
                    </li>
                </ul>
                <SignOffFormContainer />
            </div>
        </div>
    );
};


export default EndOfRoundPage;
