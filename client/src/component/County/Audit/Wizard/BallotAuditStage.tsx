import * as React from 'react';

import * as _ from 'lodash';

import { Checkbox, EditableText, MenuDivider } from '@blueprintjs/core';

import BackButton from './BackButton';
import WaitingForNextBallot from './WaitingForNextBallot';

import ballotNotFound from 'corla/action/county/ballotNotFound';
import countyFetchCvr from 'corla/action/county/fetchCvr';


interface NotFoundProps {
    ballotNotFound: OnClick;
    currentBallot: CVR;
}

const BallotNotFoundForm = (props: NotFoundProps) => {
    const { ballotNotFound, currentBallot } = props;
    const onClick = () => ballotNotFound(currentBallot.id);

    return (
        <div>
            <div className='not-found-header'> Are you looking at the right ballot?</div>
            <div className='not-found-copy'>
                Before making any selections below, first make sure the paper ballot you are examining 
                matches the Current ballot information displayed on the left. If you make selections based 
                on the wrong ballot, you may have to audit more ballots later.
            </div>
            <button className='pt-button pt-large pt-intent-danger' onClick={ onClick }>
                Ballot Not Found - Move to next ballot
            </button>
        </div>
    );
};

interface InstructionsProps {
    ballotNotFound: OnClick;
    totalBallotsForBoard: number;
    countyState: County.AppState;
    currentBallot: CVR;
    currentBallotNumber: number;
}

const AuditInstructions = (props: InstructionsProps) => {
    const {
        ballotNotFound,
        totalBallotsForBoard,
        countyState,
        currentBallot,
        currentBallotNumber,
    } = props;

    const isCurrentCvr = (cvr: JSON.CVR) => cvr.db_id === currentBallot.id;
    const fullCvr = _.find(countyState.cvrsToAudit, isCurrentCvr);
    const storageBin = fullCvr ? fullCvr.storage_location : '—';

    return (
        <div>
            <div className='current-ballot-info'>
                <h3 className='sidebar-heading'>Current ballot:</h3>
                <ul className='current-ballot-stats'>
                    <li>Scanner #{ currentBallot.scannerId }</li>
                    <li>Batch #{ currentBallot.batchId }</li>
                    <li>Ballot Position #{ currentBallot.recordId }</li>
                    <li>Storage Bin { storageBin }</li>
                    <li>Ballot type: { currentBallot.ballotType }</li>
                </ul>
            </div>
        </div>
    );
};

interface ContestInfoProps {
    contest: Contest;
}

const ContestInfo = ({ contest }: ContestInfoProps) => {
    const { name, description, choices, votesAllowed } = contest;

    return (
        <div className='contest-info'>
            <div className='contest-name'>{ name }</div>
            <div>{ description }</div>
            <div className='num-of-choices'>Vote for { votesAllowed } out of { choices.length }</div>
        </div>
    );
};

interface ChoicesProps {
    choices: ContestChoice[];
    marks: County.ACVRContest;
    noConsensus: boolean;
    updateBallotMarks: OnClick;
}

const ContestChoices = (props: ChoicesProps) => {
    const { 
        choices, 
        marks, 
        noConsensus, 
        updateBallotMarks 
    } = props;

    function updateChoiceByName(name: string) {
        function updateChoice(e: React.ChangeEvent<HTMLInputElement>) {
            const checkbox = e.target;

            updateBallotMarks({ choices: { [name]: checkbox.checked } });
        }

        return updateChoice;
    }

    const choiceForms = _.map(choices, choice => {
        const checked = marks.choices[choice.name];

        return (
            <div className='contest-choice-selection'>
                <Checkbox
                    className='rla-contest-choice'
                    key={ choice.name }
                    disabled={ noConsensus }
                    checked={ checked || false }
                    onChange={ updateChoiceByName(choice.name) } >
                    <span className='choice-name'>{choice.name}</span></Checkbox>
            </div>
        );
    });

    return (
        <div className='contest-choices'>
            { choiceForms }
        </div>
    );
};

interface CommentsProps {
    comments: string;
    onChange: OnClick;
}

const ContestComments = (props: CommentsProps) => {
    const { comments, onChange } = props;

    return (
        <div className=''>
            <label>
                Comments:
                <EditableText multiline value={ comments || '' } onChange={ onChange } />
            </label>
        </div>
    );
};

interface MarkFormProps {
    contest: Contest;
    countyState: County.AppState;
    currentBallot: CVR;
    updateBallotMarks: OnClick;
}

const BallotContestMarkForm = (props: MarkFormProps) => {
    const { contest, countyState, currentBallot, updateBallotMarks } = props;
    const { name, description, choices, votesAllowed } = contest;

    const acvr = countyState.acvrs![currentBallot.id];
    const contestMarks = acvr[contest.id];

    const updateComments = (comments: string) => {
        updateBallotMarks({ comments });
    };

    const updateConsensus = (e: React.ChangeEvent<any>) => {
        updateBallotMarks({ noConsensus: !!e.target.checked });
    };

    return (
        <div className='contest-row'>
            <ContestInfo contest={ contest } />
            <ContestChoices
                choices={ choices }
                marks={ contestMarks }
                noConsensus={ !!contestMarks.noConsensus }
                updateBallotMarks={ updateBallotMarks }
            />
            <div className=''>
                <Checkbox
                    label='No consensus'
                    checked={ !!contestMarks.noConsensus }
                    onChange={ updateConsensus }
                />
            </div>
            <ContestComments comments={ contestMarks.comments } onChange={ updateComments } />
        </div>
    );
};

interface AuditFormProps {
    countyState: County.AppState;
    currentBallot: CVR;
    updateBallotMarks: OnClick;
}

const BallotAuditForm = (props: AuditFormProps) => {
    const { countyState, currentBallot } = props;

    const contestForms = _.map(currentBallot.contestInfo, info => {
        const contest = countyState.contestDefs![info.contest];

        const updateBallotMarks = (data: any) => props.updateBallotMarks({
            ballotId: currentBallot.id,
            contestId: contest.id,
            ...data,
        });

        return (
            <BallotContestMarkForm
                key={ contest.id }
                contest={ contest }
                countyState={ countyState }
                currentBallot={ currentBallot }
                updateBallotMarks={ updateBallotMarks } />
        );
    });

    return <div>{ contestForms }</div>;
};

interface StageProps {
    auditBoardIndex: number;
    countyState: County.AppState;
    currentBallot: County.CurrentBallot;
    currentBallotNumber: number;
    nextStage: OnClick;
    prevStage: OnClick;
    totalBallotsForBoard: number;
    updateBallotMarks: OnClick;
}


const BallotAuditStage = (props: StageProps) => {
    const {
        auditBoardIndex,
        countyState,
        currentBallot,
        currentBallotNumber,
        nextStage,
        prevStage,
        totalBallotsForBoard,
        updateBallotMarks,
    } = props;

    const notFound = () => {
        ballotNotFound(currentBallot.id);
    };

    const { currentRound } = countyState;

    if (currentBallot.submitted) {
        return <WaitingForNextBallot />;
    }

    return (
        <div className='rla-page'>
            <div className='audit-page-container'>
                <div className='audit-page-header'>
                    <h2 className='audit-page-title'>Audit Board { `${auditBoardIndex + 1}` }: Ballot Card Verification</h2>
                    <div className='audit-page-subtitle'>Enter ballot information</div>
                    <div className='ballot-number'>Auditing ballot card { currentBallotNumber } of { currentRound!.expectedCount }</div>
                </div>

                <div className='col-layout row1'>
                    <div className='col1'>
                        <AuditInstructions
                            ballotNotFound={ notFound }
                            countyState={ countyState }
                            currentBallot={ currentBallot }
                            currentBallotNumber={ currentBallotNumber }
                            totalBallotsForBoard={ totalBallotsForBoard } />
                    </div>
                    <div className='col2'>
                        <BallotNotFoundForm
                            ballotNotFound={ ballotNotFound }
                            currentBallot={ currentBallot } />
                    </div>
                </div>

                <div className='col-layout'>
                    <div className='col1'>
                        <div className='sidebar-instructions'>
                            <h3 className='sidebar-heading'>How to match selections with ballot</h3>
                            <div className='sidebar-accordion'>
                                <input type='checkbox' id='item1' name='accordion1' className='accordion-item'/>
                                <label htmlFor='item1' className='accordion-item-label'>Overvote</label>
                                <div id='content1' className= 'accordion-item-content'>
                                    <p>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eleifend id leo quis rutrum. Curabitur condimentum augue et dolor sodales, vel iaculis lacus feugiat.
                                    </p>
                                </div>

                                <input type='checkbox' id = 'item2' name = 'accordion1' className='accordion-item' />
                                <label htmlFor='item2' className='accordion-item-label'>Undervote</label>
                                <div id = 'content2' className= 'accordion-item-content'>
                                    <p>
                                        Cras pretium blandit nunc, maximus convallis elit eleifend id. Nullam non posuere eros, sit amet iaculis leo. Morbi tincidunt nibh quis magna dapibus, sit amet imperdiet massa blandit. Maecenas sapien erat, efficitur id est sed, facilisis malesuada magna. Vivamus interdum non urna vel placerat. Duis sed nunc nec mi posuere tempor at sit amet dui. Curabitur faucibus sapien a massa vestibulum, in bibendum orci pharetra.
                                    </p>
                                </div>

                                <input type = 'checkbox' id = 'item3' name = 'accordion1' className='accordion-item' />
                                <label htmlFor='item3' className='accordion-item-label'>Write-in</label>
                                <div id = 'content3' className= 'accordion-item-content'>
                                    <p>
                                        Cras pretium blandit nunc, maximus convallis elit eleifend id. Nullam non posuere eros, sit amet iaculis leo. Morbi tincidunt nibh quis magna dapibus, sit amet imperdiet massa blandit. Maecenas sapien erat, efficitur id est sed, facilisis malesuada magna. Vivamus interdum non urna vel placerat. Duis sed nunc nec mi posuere tempor at sit amet dui. Curabitur faucibus sapien a massa vestibulum, in bibendum orci pharetra.
                                    </p>
                                </div>

                                <input type = 'checkbox' id = 'item4' name = 'accordion1' className='accordion-item' />
                                <label htmlFor='item4' className='accordion-item-label'>We can't agree</label>
                                <div id = 'content4' className= 'accordion-item-content'>
                                    <p>
                                        Cras pretium blandit nunc, maximus convallis elit eleifend id. Nullam non posuere eros, sit amet iaculis leo. Morbi tincidunt nibh quis magna dapibus, sit amet imperdiet massa blandit. Maecenas sapien erat, efficitur id est sed, facilisis malesuada magna. Vivamus interdum non urna vel placerat. Duis sed nunc nec mi posuere tempor at sit amet dui. Curabitur faucibus sapien a massa vestibulum, in bibendum orci pharetra.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className='col2'>
                        <BallotAuditForm
                            countyState={ countyState }
                            currentBallot={ currentBallot }
                            updateBallotMarks={ updateBallotMarks } />
                    </div>
                </div>
                <div className='button-container'>
                    <BackButton back={ prevStage } />

                    <button className='pt-large pt-button pt-intent-success pt-breadcrumb' onClick={ nextStage }>
                        Review
                    </button>
                </div>
            </div>
        </div>
    );
};


export default BallotAuditStage;
