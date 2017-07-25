import * as React from 'react';

import CountyNav from './Nav';


const Main = ({ name }: any) => (
    <div>
        <h1>Hello, { name }!</h1>
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

const ContestInfo = (contests: any) => (
    <div>
        <div>Contest info</div>
        <div>{ JSON.stringify(contests) }</div>
    </div>
);

const CountyInfo = (info: any) => (
    <div>
        <div>County Info</div>
        <div>{ JSON.stringify(info) }</div>
    </div>
);

const CountyRootPage = ({ name, info, contests }: any) => {
    return (
        <div>
            <CountyNav />
            <div>
                <Main name={ name } />
                <CountyInfo info={ info } />
                <ContestInfo contests={ contests } />
            </div>
        </div>
    );
};

export default CountyRootPage;
