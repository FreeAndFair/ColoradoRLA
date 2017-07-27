import * as React from 'react';

import * as _ from 'lodash';

import BallotManifestUploaderContainer from '../../container/county/home/BallotManifestUploaderContainer';
import CVRUploaderContainer from '../../container/county/home/CVRUploaderContainer';
import CountyNav from './Nav';


const Main = ({ name }: any) => (
    <div className='county-main pt-card'>
        <h1>Hello, { name }!</h1>
        <div>
            <div>
                Please upload your Ballot Manifest and Cast Vote Records.
            </div>
            <BallotManifestUploaderContainer />
            <CVRUploaderContainer />
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
    <div className='pt-card'>
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
    <div className='contest-info pt-card'>
        <div>Contest info</div>
        <div>
            { _.map(contests, (c: any) => <ContestInfoTable key={ c.name } { ...c }/>) }
        </div>
    </div>
);

const CountyInfo = (info: any) => (
    <div className='county-info pt-card'>
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
    <div className='info pt-card'>
        <CountyInfo info={ info } />
        <ContestInfo contests={ contests } />
    </div>
);

const CountyRootPage = ({ name, info, contests }: any) => {
    return (
        <div className='county-root'>
            <CountyNav />
            <div>
                <Main name={ name } />
                <Info info={ info } contests={ contests } />
            </div>
        </div>
    );
};

export default CountyRootPage;
