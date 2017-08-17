import * as React from 'react';

import * as _ from 'lodash';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';

import findById from '../../../../findById';

import BackButton from './BackButton';


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
            <div className='pt-card'>
                Record here the <strong> voter intent </strong> as described by the Voter
                Intent Guide from the Secretary of State. All markings <strong> do not </strong>
                need to be recorded. Replicate on this page all <strong> valid votes </strong> in
                each ballot contest contained on this paper ballot. Or, in case of an
                <strong> overvote</strong>, record all final voter choices that contribute to
                the overvote. Please include notes in the comments field.
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

const ContestChoices = (props: any) => {
    const { choices, marks, noConsensus, updateBallotMarks } = props;

    const updateChoiceById = (id: number) => (e: any) => {
        const nextChoices = _.without(marks.choices, id);

        if (e.target.checked) {
            nextChoices.push(id);
        }

        updateBallotMarks({ choices: nextChoices });
    };

    const choiceForms = _.map(choices, (c: any) => {
        const checked = _.includes(marks.choices, c.id);

        return (
            <Checkbox
                key={ c.id }
                disabled={ noConsensus }
                checked={ checked }
                onChange={ updateChoiceById(c.id) }
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

    const contestMarks = marks[contest.id];

    const noConsensus = !!contestMarks.noConsensus;

    const updateComments = (comments: any) => {
        updateBallotMarks({ comments });
    };

    const updateConsensus = (e: any) => {
        updateBallotMarks({ noConsensus: e.target.checked });
    };

    return (
        <div className='pt-card'>
            <ContestInfo contest={ contest } />
            <ContestChoices
                choices={ choices }
                marks={ contestMarks }
                noConsensus={ noConsensus }
                updateBallotMarks={ updateBallotMarks }
            />
            <div className='pt-card'>
                <Checkbox
                    label='No consensus'
                    checked={ noConsensus }
                    onChange={ updateConsensus }
                />
            </div>
            <ContestComments comments={ contestMarks.comments } onChange={ updateComments } />
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
    const {
        ballots,
        county,
        nextStage,
        prevStage,
        updateBallotMarks,
    } = props;

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
            <BackButton back={ prevStage } />
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Review
            </button>
        </div>
    );
};


export default BallotAuditStage;
