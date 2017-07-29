import * as React from 'react';

import * as _ from 'lodash';

import findById from '../../../../findById';


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

const BallotContestReview = ({ comments, contest, marks }: any) => {
    const markDOM = _.map(marks, (m: any) => (
        <div key={ m.id } className='pt-card'>
            <div>{ m.name }</div>
        </div>
    ));

    return (
        <div className='pt-card'>
            <div className='pt-card'>
                <div>{ contest.name }</div>
                <div>{ contest.description }</div>
                <div>Vote for { contest.votesAllowed }</div>
            </div>
            <div className='pt-card'>
                { markDOM }
            </div>
            <div className='pt-card'>
                { comments }
            </div>
        </div>
    );
};

const BallotReview = ({ ballotMarks }: any) => {
    const contestReviews = _.map(ballotMarks, (contestMarks: any) => {
        const key = contestMarks.contest.id;
        return <BallotContestReview key={ key } { ...contestMarks } />;
    });

    return <div className='pt-card'>{ contestReviews }</div>;
};

const ReviewStage = (props: any) => {
    const {
        county,
        currentBallot,
        marks: rawMarks,
        nextStage,
        selectNextBallot,
    } = props;

    const ballotMarks = _.mapValues(rawMarks, ({choices, comments }: any, contestId: any) => {
        const contest = findById(county.contests, contestId);
        const marks = _.map(choices, (id: any) => findById(contest.choices, id));

        return { contest, marks, comments };
    });

    const onClick  = () => {
        selectNextBallot();
        nextStage();
    };

    return (
        <div>
            <BallotReview ballotMarks={ ballotMarks } />
            <div className='pt-card'>
                <button className='pt-button pt-intent-primary' onClick={ onClick }>
                    Submit & Next Ballot
                </button>
            </div>
        </div>
    );
};


export default ReviewStage;
