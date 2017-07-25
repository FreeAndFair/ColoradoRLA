import * as React from 'react';

import CountyNav from './Nav';


const Main = () => (
    <div>
        <div>Your status:</div>
        <div>
            <div>Please upload your Ballot Manifest and Cast Vote Records.</div>
            <div>
                Ballot Manifest:
                <button className='pt-button pt-intent-primary pt-icon-add'>
                    Upload
                </button>
            </div>
            <div>
                Cast Vote Records:
                <button className='pt-button pt-intent-primary pt-icon-add'>
                    Upload
                </button>
            </div>
        </div>
    </div>
);

const ContestInfo = () => (
    <div>Contest info</div>
);

const CountyInfo = () => (
    <div>County info</div>
);

const CountyRootPage = () => {
    return (
        <div>
            <CountyNav />
            <div>
                <Main />
                <CountyInfo />
                <ContestInfo />
            </div>
        </div>
    );
};

export default CountyRootPage;
