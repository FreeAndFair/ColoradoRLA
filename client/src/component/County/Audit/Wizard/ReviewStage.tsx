import * as React from 'react';

import * as _ from 'lodash';

import BackButton from './BackButton';
import SubmittingACVR from './SubmittingACVR';


interface BallotContestReviewProps {
    contest: Contest;
    marks: County.ACVRContest;
}

const BallotContestReview = (props: BallotContestReviewProps) => {
    const { contest, marks } = props;
    const { comments, noConsensus } = marks;
    const { votesAllowed } = contest;

    const markedChoices: County.ACVRChoices = _.pickBy(marks.choices);
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

    const markedChoiceDivs = _.map(markedChoices, (_, name) => {
        return (
            <div key={ name } className='pt-card'>
                <strong><div>{ name }</div></strong>
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
                Votes for:
                { markedChoiceDivs.length ? markedChoiceDivs : noMarksDiv }
            </div>
        );
    };

    return (
        <div className='pt-card'>
            <div className='pt-card'>
                <strong><div>{ contest.name }</div></strong>
                <strong><div>{ contest.description }</div></strong>
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

interface BallotReviewProps {
    countyState: County.AppState;
    marks: County.ACVR;
}

const BallotReview = (props: BallotReviewProps) => {
    const { countyState, marks } = props;

    const contestReviews = _.map(marks, (m, contestId) => {
        const contest = countyState.contestDefs![contestId];

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

interface ReviewStageProps {
    countyState: County.AppState;
    currentBallot: County.CurrentBallot;
    marks: County.ACVR;
    nextStage: OnClick;
    prevStage: OnClick;
    uploadAcvr: OnClick;
}

const ReviewStage = (props: ReviewStageProps) => {
    const {
        countyState,
        currentBallot,
        marks,
        nextStage,
        prevStage,
        uploadAcvr,
    } = props;

    async function onClick() {
        const m = countyState!.acvrs![currentBallot.id];

        try {
            await uploadAcvr(m, currentBallot);
            nextStage();
        } catch {
            // Failed to submit. Let saga machinery alert user.
        }
    }

    if (currentBallot.submitted) {
        return <SubmittingACVR />;
    }

    return (
        <div className='rla-page'>
            <BallotReview countyState={ countyState } marks={ marks } />
            <div className='pt-card'>
                <BackButton back={ prevStage } />
                <button className='pt-button pt-intent-primary pt-breadcrumb' onClick={ onClick }>
                    Submit & Next Ballot Card
                </button>
            </div>
        </div>
    );
};


export default ReviewStage;
