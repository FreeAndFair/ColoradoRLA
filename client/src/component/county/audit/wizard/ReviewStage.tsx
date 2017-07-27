import * as React from 'react';


const BallotContestResultVoteForN = () => (
    <div className='pt-card'>
        <div className='pt-card'>
            <div>Ballot contest 1</div>
            <div>Acme County School District RE-1</div>
            <div>Director</div>
        </div>
        <div className='pt-card'>
            <div>Choice B</div>
            <div>Choice C</div>
        </div>
    </div>
);

const BallotContestResultYesNo = () => (
    <div className='pt-card'>
        <div className='pt-card'>
            <div>Ballot contest 2</div>
            <div>Prop 101</div>
        </div>
        <div className='pt-card'>
            Yes
        </div>
    </div>
);

const BallotContestResultUndervote = () => (
    <div className='pt-card'>
        <div className='pt-card'>
            <div>Ballot contest 3</div>
            <div>Governor</div>
        </div>
        <div className='pt-card'>
            Undervote
        </div>
        <div className='pt-card'>
            Comments: Faint markings visible.
        </div>
    </div>
);

const BallotContestResults = () => (
    <div className='pt-card'>
        <BallotContestResultVoteForN />
        <BallotContestResultYesNo />
        <BallotContestResultUndervote />
    </div>
);


const ReviewStage = ({ nextStage }: any) => (
    <div>
        <BallotContestResults />
        <div className='pt-card'>
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Submit & Next Ballot
            </button>
        </div>
    </div>
);


export default ReviewStage;
