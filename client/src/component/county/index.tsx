import * as React from 'react';

import * as _ from 'lodash';

import CountyNav from './Nav';


const Main = ({ name }: any) => (
    <div>
        <h1>Hello, { name }!</h1>
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

const ContestInfoTableRow = (choice: any) => (
    <tr key={ choice.id }>
        <td>{ choice.id }</td>
        <td>{ choice.name }</td>
    </tr>
);

const ContestInfoTable = (contest: any) => (
    <div>
        <span>{ contest.name }</span>
        <table className='pt-table'>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
            </thead>
            <tbody>
                { _.map(contest.choices, (c: any) => <ContestInfoTableRow key={ c.id } { ...c }/> ) }
            </tbody>
        </table>
    </div>
);

const ContestInfo = ({ contests }: any): any => (
    <div>
        <div>Contest info</div>
        <div>
            { _.map(contests, (c: any) => <ContestInfoTable key={ c.name } { ...c }/>) }
        </div>
    </div>
);

const CountyInfo = (info: any) => (
    <div>
        <div>County Info</div>
        <div>
            <span>Field1</span>
            <span>{ info.field1 }</span>
        </div>
        <div>
            <span>Field2</span>
            <span>{ info.field2 }</span>
        </div>
    </div>
);

const Info = ({ info, contests }: any) => (
    <div>
        <CountyInfo info={ info } />
        <ContestInfo contests={ contests } />
    </div>
);

const CountyRootPage = ({ name, info, contests }: any) => {
    return (
        <div>
            <CountyNav />
            <div>
                <Main name={ name } />
                <Info info={ info } contests={ contests } />
            </div>
        </div>
    );
};

export default CountyRootPage;
