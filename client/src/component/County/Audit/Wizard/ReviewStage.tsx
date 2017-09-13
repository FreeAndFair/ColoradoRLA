import * as React from 'react';

import * as _ from 'lodash';

import BackButton from './BackButton';


const BallotContestReview = ({ contest, marks }: any) => {
    const { comments, noConsensus } = marks;
    const { votesAllowed } = contest;

    const markedChoices: any = _.pickBy(marks.choices);
    const votesMarked = _.size(markedChoices);

    const noConsensusDiv = (
        <div>
            The Audit Board did not reach consensus.
        </div>
    );

    const noMarksDiv = (
        <div>
            Blank Vote
        </div>
    );

    const markedChoiceDivs = _.map(markedChoices, (_: any, name: any) => {
        return (
            <div key={ name } className='pt-card'>
                <div>{ name }</div>
            </div>
        );
    });

    const renderMarkedChoices = () => {
        if (votesMarked > votesAllowed) {
            return (
                <div>
                    <strong>Overvote</strong> for this contest.
                </div>
            );
        }

        return (
            <div>
                <strong>Votes for:</strong>
                { markedChoiceDivs.length ? markedChoiceDivs : noMarksDiv }
            </div>
        );
    };

    return (
        <div className='pt-card'>
            <div className='pt-card'>
                <div>{ contest.name }</div>
                <div>{ contest.description }</div>
                <div>Vote for { contest.votesAllowed }</div>
            </div>
            <div className='pt-card'>
                { noConsensus ? noConsensusDiv : renderMarkedChoices() }
            </div>
            <div className='pt-card'>
                Comments: { comments }
            </div>
        </div>
    );
};

const BallotReview = ({ county, marks }: any) => {
    const contestReviews = _.map(marks, (m: any, contestId: any) => {
        const contest = county.contestDefs[contestId];

        return (
            <BallotContestReview
                key={ contestId }
                contest={ contest }
                marks={ m }
            />
        );
    });

    return <div className='pt-card'>{ contestReviews }</div>;
};

const ReviewStage = (props: any) => {
    const {
        county,
        currentBallot,
        marks,
        nextStage,
        prevStage,
        uploadAcvr,
    } = props;

    const onClick = () => {
        const m = county.acvrs[currentBallot.id];

        uploadAcvr(m, currentBallot);
        nextStage();
    };

    return (
        <div className='rla-page'>
            <BallotReview county={ county } marks={ marks } />
            <div className='pt-card'>
                <BackButton back={ prevStage } />
                <button className='pt-button pt-intent-primary' onClick={ onClick }>
                    Submit & Next Ballot Card
                </button>
            </div>
        </div>
    );
};


export default ReviewStage;
