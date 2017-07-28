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

const BallotContestReview = ({ contest, marks }: any) => {
    const markDOM = _.map(marks, (m: any) => (
        <div key={ m.id } className='pt-card'>
            <div>{ m.id }</div>
            <div>{ m.name }</div>
        </div>
    ));

    return (
        <div className='pt-card'>
            <div className='pt-card'>
                <div>{ contest.name } ({ contest.id })</div>
                <div>{ contest.description }</div>
                <div>Vote for { contest.votesAllowed }</div>
            </div>
            <div className='pt-card'>
                { markDOM }
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
    const { county, nextStage } = props;

    // `exampleMarks` : { [ContestId]: ChoiceId[] }
    const exampleMarks = {
        1: { choices: ['2'], comments: '' },
        3: { choices: ['8', '9', '11'], comments: '' },
    };

    const ballotMarks = _.mapValues(exampleMarks, ({choices, comments }: any, contestId: any) => {
        const contest = findById(county.contests, contestId);
        const marks = _.map(choices, (id: any) => findById(contest.choices, id));

        return { contest, marks, comments };
    });

    return (
        <div>
            <BallotReview ballotMarks={ ballotMarks } />
            <div className='pt-card'>
                <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                    Submit & Next Ballot
                </button>
            </div>
        </div>
    );
};


export default ReviewStage;
