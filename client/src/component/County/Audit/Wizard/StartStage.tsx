import * as React from 'react';

import counties from 'corla/data/counties';

import * as corlaDate from 'corla/date';
import * as format from 'corla/format';


interface StageProps {
    county: CountyState;
    nextStage: OnClick;
}

const StartStage = (props: StageProps) => {
    const { county, nextStage } = props;
    const { election } = county;

    const countyName = counties[county!.id!]!.name;
    const electionDate = corlaDate.format(election!.date);
    const electionType = format.electionType(election!.type);

    return (
        <div className='rla-page'>
            <h2>{ countyName } County { electionDate } { electionType }</h2>
            <div className='pt-card'>
                Audit Board Reporting Utility for Risk-Limiting Audit
            </div>
            <div className='pt-card'>
                Click here to proceed: <span> </span>
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
