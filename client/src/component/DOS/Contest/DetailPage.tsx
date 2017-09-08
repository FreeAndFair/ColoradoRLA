import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import Nav from '../Nav';


const Breadcrumb = ({ contest }: any) => (
    <ul className='pt-breadcrumbs'>
        <li>
            <Link to='/sos'>
                <div className='pt-breadcrumb pt-disabled'>
                    SoS
                </div>
            </Link>
        </li>
        <li>
            <Link to='/sos/contest'>
                <div className='pt-breadcrumb'>
                    Contest
                </div>
            </Link>
        </li>
        <li>
            <div className='pt-breadcrumb pt-breadcrumb-current'>
                { contest.name }
            </div>
        </li>
    </ul>
);

const ContestChoices = ({ contest }: any) => {
    const choiceItems = _.map(contest.choices, (c: any, k: any) => (
        <li key={ k }>{ c.name }</li>
    ));

    return (
        <div className='pt-card'>
            <h4>Choices:</h4>
            <ul>{ choiceItems }</ul>
        </div>
    );
};

const ContestDetailPage = (props: any) => {
    const { contest } = props;

    const row = (k: any, v: any) => (
        <tr key={ k } >
            <td>{ k }</td>
            <td>{ v }</td>
        </tr>
    );

    return (
        <div>
            <Nav />
            <Breadcrumb contest={ contest } />
            <h2>Status</h2>
            <div className='pt-card'>
                <h3>Contest Data</h3>
                <table className='pt-table pt-bordered pt-condensed'>
                    <tbody>
                        { row('ID', contest.id) }
                        { row('Name', contest.name) }
                        { row('Description', contest.description) }
                        { row('Vote For', contest.votesAllowed) }
                        { row('Ballot Manifest', 'Uploaded') }
                        { row('CVR Export', 'Uploaded') }
                    </tbody>
                </table>
            </div>
            <ContestChoices contest={ contest } />
        </div>
    );
};


export default ContestDetailPage;
