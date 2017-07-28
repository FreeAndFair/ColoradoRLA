import * as React from 'react';

import * as _ from 'lodash';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import findById from '../../../../findById';


const AuditInstructions = ({ ballotsToAudit, currentBallot }: any) => (
    <div className='pt-card'>
        <div>
            Use this page to report the voter markings on the ballot with ID
            #{ currentBallot.id }, out of { ballotsToAudit } ballots that you must
            audit.
        </div>
        <div>
            The current ballot is:
            <ul>
                <li>{ currentBallot.id }</li>
                <li>{ currentBallot.style.name }</li>
            </ul>
            <div>
                Please ensure that the paper ballot you are examining is the
                same ballot style/ID.
            </div>
            <div>
                Replicate on this page all valid votes in each ballot contest
                contained on this paper ballot.  If you determine a particular
                ballot contest does not contain a valid vote, please also
                indicate whether the ballot contains a blank vote or an over
                vote.
            </div>
            <div>
                If the paper ballot you are examining indicates that it was
                duplicated, please ask a county staff member to retrieve the
                original paper ballot submitted by the voter, and report the
                votes contained on that ballot on this page.
            </div>
        </div>
    </div>
);

const ContestInfo = ({ contest }: any) => {
    const { name, description, choices, votesAllowed } = contest;

    return (
        <div className='pt-card'>
            <div>{ name }</div>
            <div>{ description }</div>
            <div>Vote for { votesAllowed } out of { choices.length }</div>
        </div>
    );
};

const ContestChoices = ({ choices, updateBallotMarks }: any) => {
    const updateChoices = (e: any) => {
        let { checked } = e.target;
        updateBallotMarks({});
    };

    const choiceForms = _.map(choices, (c: any) => {
        return (
            <Checkbox
                key={ c.id }
                checked={ false }
                onChange={ updateChoices }
                label={ c.name }
            />
        );
    });

    return (
        <div className='pt-card'>
            { choiceForms }
        </div>
    );
};

const ContestComments = ({ comments, onChange }: any) => {
    return (
        <div className='pt-card'>
            <label>
                Comments:
                <EditableText multiline value={ comments } onChange={ onChange } />
            </label>
        </div>
    );
};

const BallotContestMarkForm = (props: any) => {
    const { contest, county, currentBallot, updateBallotMarks } = props;
    const { name, description, choices, votesAllowed } = contest;
    const { marks } = currentBallot;

    const updateComments = (comments: any) => {
        updateBallotMarks({ comments });
    };

    const updateConsensus = (e: any) => {
        updateBallotMarks({});
    };

    return (
        <div className='pt-card'>
            <ContestInfo contest={ contest } />
            <ContestChoices
                choices={ choices }
                updateBallotMarks={ updateBallotMarks }
            />
            <div className='pt-card'>
                <Checkbox
                    label='No consensus'
                    checked={ false }
                    onChange={ updateConsensus }
                />
            </div>
            <ContestComments comments={ marks.comments } onChange={ updateComments } />
        </div>
    );
};

const BallotAuditForm = (props: any) => {
    const { county, currentBallot } = props;

    const contestForms = _.map(currentBallot.style.contests, (c: any) => {
        const updateBallotMarks: any = (data: any) => props.updateBallotMarks({
            ballotId: currentBallot.id,
            contestId: c.id,
            ...data,
        });

        return (
            <BallotContestMarkForm
                key={ c.id }
                contest={ c }
                county={ county }
                currentBallot={ currentBallot }
                updateBallotMarks={ updateBallotMarks } />
        );
    });

    return <div>{ contestForms }</div>;
};

const BallotAuditStage = (props: any) => {
    const { ballotStyles, ballots, county, nextStage, updateBallotMarks } = props;

    const ballotsToAudit = county.ballots.length;
    const currentBallot = findById(county.ballots, county.currentBallotId);

    return (
        <div>
            <h2>Ballot verification</h2>
            <AuditInstructions
                ballotsToAudit={ ballotsToAudit }
                currentBallot={ currentBallot }
            />
            <BallotAuditForm
                county={ county }
                currentBallot={ currentBallot }
                updateBallotMarks={ updateBallotMarks }
            />
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Review
            </button>
        </div>
    );
};


export default BallotAuditStage;
