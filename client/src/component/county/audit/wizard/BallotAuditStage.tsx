import * as React from 'react';

import * as _ from 'lodash';

import { Checkbox, EditableText, Radio, RadioGroup } from '@blueprintjs/core';


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

const ContestChoices = ({ choices }: any) => {
    const nop = () => ({});
    const choiceForms = _.map(choices, (c: any) => (
        <Checkbox
            key={ c.id }
            checked={ false }
            onChange={ nop }
            label={ c.name } />
    ));

    return (
        <div className='pt-card'>
            { choiceForms }
        </div>
    );
};

const ContestComments = ({ onChange }: any) => {
    return (
        <div className='pt-card'>
            <label>
                Comments:
                <EditableText multiline onChange={ onChange } />
            </label>
        </div>
    );
};

const BallotContestMarkForm = ({ contest }: any) => {
    const { name, description, choices, votesAllowed } = contest;
    const nop = () => ({});

    return (
        <div className='pt-card'>
            <ContestInfo contest={ contest } />
            <ContestChoices choices={ choices } />
            <div className='pt-card'>
                <Checkbox checked={ false } onChange={ nop } label='No consensus' />
            </div>
            <ContestComments onChange={ nop } />
        </div>
    );
};

const BallotAuditForm = (props: any) => {
    const { county, currentBallot } = props;

    const contestForms = _.map(currentBallot.style.contests, (c: any) => {
        return <BallotContestMarkForm key={ c.id } contest={ c } />;
    });

    return <div>{ contestForms }</div>;
};

const BallotAuditStage = (props: any) => {
    const { ballotStyles, county, nextStage } = props;

    const ballotsToAudit = county.ballots.length;
    const currentBallot = _.find(county.ballots, (b: any) =>
        b.id === county.currentBallotId);

    return (
        <div>
            <h2>Ballot verification</h2>
            <AuditInstructions
                ballotsToAudit={ ballotsToAudit }
                currentBallot={ currentBallot } />
            <BallotAuditForm
                county={ county }
                currentBallot={ currentBallot } />
            <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                Review
            </button>
        </div>
    );
};


export default BallotAuditStage;
