import * as React from 'react';

import * as _ from 'lodash';

import SubmittingACVR from './SubmittingACVR';

interface EditButtonProps {
    back: OnClick;
}

const EditButton = ({ back }: EditButtonProps) => {
    return (
        <a onClick={ back }>
            <span className='pt-icon-standard pt-icon-edit review-edit-selection'></span>
            Edit
        </a>
    );
};

interface InstructionsProps {
    countyState: County.AppState;
    currentBallot: CVR;
}

const AuditInstructions = (props: InstructionsProps) => {
    const {
        countyState,
        currentBallot,
    } = props;

    const { currentRound } = countyState;
    const isCurrentCvr = (cvr: JSON.CVR) => cvr.db_id === currentBallot.id;
    const fullCvr = _.find(countyState.cvrsToAudit, isCurrentCvr);
    const storageBin = fullCvr ? fullCvr.storage_location : '—';

    return (
        <div>
            <div className='current-ballot-info'>
                <h3 className='sidebar-heading'>Current ballot:</h3>
                <ul className='current-ballot-stats'>
                    <li>Storage bin: <span className='pt-ui-text pt-ui-text-large'>{ storageBin }</span></li>
                    <li>Tabulator: <span className='pt-ui-text pt-ui-text-large'>{ currentBallot.scannerId }</span></li>
                    <li>Batch: <span className='pt-ui-text pt-ui-text-large'>{ currentBallot.batchId }</span></li>
                    <li>Ballot position: <span className='pt-ui-text pt-ui-text-large'>{ currentBallot.recordId }</span></li>
                    <li>Ballot type: <span className='pt-ui-text pt-ui-text-large'>{ currentBallot.ballotType }</span></li>
                </ul>
            </div>
        </div>
    );
};

interface BallotContestReviewProps {
    back: OnClick;
    contest: Contest;
    marks: County.ACVRContest;
}

const BallotContestReview = (props: BallotContestReviewProps) => {
    const { back, contest, marks } = props;
    const { comments, noConsensus } = marks;
    const { votesAllowed } = contest;

    const markedChoices: County.ACVRChoices = _.pickBy(marks.choices);
    const votesMarked = _.size(markedChoices);

    const noConsensusDiv = (
        <div>
            <span className='no-consensus pt-icon-standard pt-icon-disable'></span>
            No audit board consensus
        </div>
    );

    const noMarksDiv = (
        <div>
            <span className='blank-vote pt-icon-standard pt-icon-cross'></span>
            Blank vote - no mark
        </div>
    );

    const commentsDiv = (
      <div className='comments-container'>
        <div className='comments-head'>COMMENT:</div>
        <div className='comments-body'>{ comments }</div>
      </div>
    );

    const markedChoiceDivs = _.map(markedChoices, (_, name) => {
        return (
            <div className='rla-contest-choice' key={ name }>
                <span className='choice-name'>{ name }</span>
            </div>
        );
    });

    const renderMarkedChoices = () => {
        if (votesMarked > votesAllowed) {
            return (
                <div>
                    <div className='contest-choice-selection contest-choice-review'>
                        { markedChoiceDivs.length ? markedChoiceDivs : noMarksDiv }
                    </div>
                    <strong>Overvote</strong> for this contest.
                </div>
            );
        }

        return (
            <div className='contest-choice-selection contest-choice-review'>
                { markedChoiceDivs.length ? markedChoiceDivs : noMarksDiv }
            </div>
        );
    };

    return (
      <div className='contest-row'>
        <div className='contest-info'>
          <strong><div className='contest-name'>{ contest.name }</div></strong>
          <strong><div>{ contest.description }</div></strong>
          <div>Vote for { contest.votesAllowed }</div>
        </div>
        <div className='contest-choice-review-grid'>
          { noConsensus ? noConsensusDiv : renderMarkedChoices() }
          <div className='edit-button'>
            <EditButton back={ back } />
          </div>
        </div>
        { comments ? commentsDiv : null }
      </div>
    );
};

interface BallotReviewProps {
    back: OnClick;
    countyState: County.AppState;
    marks: County.ACVR;
}

const BallotReview = (props: BallotReviewProps) => {
    const { back, countyState, marks } = props;

    const contestReviews = _.map(marks, (m, contestId) => {
        const contest = countyState.contestDefs![contestId];

        return (
          <BallotContestReview
              back={ back }
              contest={ contest }
              key={ contestId }
              marks={ m }
          />
        );
    });

    return <div>{ contestReviews }</div>;
};

interface ReviewStageProps {
    countyState: County.AppState;
    currentBallot: County.CurrentBallot;
    currentBallotNumber: number;
    marks: County.ACVR;
    nextStage: OnClick;
    prevStage: OnClick;
    uploadAcvr: OnClick;
    totalBallotsForBoard: number;
}

const ReviewStage = (props: ReviewStageProps) => {
    const {
        countyState,
        currentBallot,
        currentBallotNumber,
        marks,
        nextStage,
        prevStage,
        uploadAcvr,
        totalBallotsForBoard,
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
            <div className='audit-page-container'>
                <div className='audit-page-header'>
                    <h2 className='audit-page-title'>Ballot Card Verification</h2>
                    <div className='audit-page-subtitle'>Review ballot</div>
                    <div className='ballot-number'>Auditing ballot card { currentBallotNumber } of { totalBallotsForBoard }</div>
                </div>

                <div className='col-layout row1'>
                    <div className='col1'>
                        <AuditInstructions countyState={ countyState } currentBallot={ currentBallot } />
                    </div>
                    <div className='col2'>
                        <p>
                            Confirm that the information displayed accurately
                            reflects its interpretation for each contest and
                            choice from the corresponding paper ballot.
                        </p>
                        <p>
                            If there are any discrepancies, click the
                            <b> Edit</b> button located to the right of the
                            audited contest’s selections, and reenter the voter
                            markings for the ballot.
                        </p>
                        <p>
                            If the review page accurately reflects the audit
                            board’s interpretation of all votes in all contests,
                            click the <b>Submit</b> button at the bottom of the
                            page.
                        </p>
                        <BallotReview countyState={ countyState } marks={ marks } back={ prevStage } />
                        <div className='button-container button-container-left'>
                          <button className='pt-large pt-button pt-intent-success pt-breadcrumb' onClick={ onClick }>
                              Submit & Next Ballot Card
                          </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};


export default ReviewStage;
