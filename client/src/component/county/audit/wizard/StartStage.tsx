import * as React from 'react';


const StartStage = ({ nextStage }: any) => (
    <div>
        <div>Acme County Audit</div>
        <div className='pt-card'>
            <div>Acme County General Election Audit</div>
            <div>Election date: 11/21/2017</div>
            <div>County & State Ballot Contests</div>
            <button className='pt-button' onClick={ nextStage }>
                Start My Audit
            </button>
        </div>
    </div>
);


export default StartStage;
