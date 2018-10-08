import * as React from 'react';

import counties from 'corla/data/counties';

import * as corlaDate from 'corla/date';
import * as format from 'corla/format';


interface StageProps {
    countyState: County.AppState;
    nextStage: OnClick;
}

const StartStage = (props: StageProps) => {
    const { countyState, nextStage } = props;
    const { election } = countyState;

    if (!election) { return null; }

    if (!countyState.id) { return null; }

    // TODO: This should happen in a container or reducer.
    const county = counties[countyState.id];

    if (!county) { return null; }

    const countyName = county.name;
    const electionDate = corlaDate.format(election.date);
    const electionType = format.electionType(election.type);

    return (
        <div className='rla-page'>
            <h2>{ countyName } County { electionDate } { electionType }</h2>
            <div className='pt-card'>
                <strong>Audit Board Reporting Utility for Risk-Limiting Audit</strong>
            </div>
            <div className='pt-card'>
                <strong>Click here to proceed:</strong> <span> </span>
                <button
                    className='pt-button pt-intent-primary'
                    onClick={ nextStage }>
                    Next
                </button>
            </div>
        </div>
    );
};


export default StartStage;
