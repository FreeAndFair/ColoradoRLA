import * as React from 'react';

import * as _ from 'lodash';

import Nav from '../Nav';


const Breadcrumb = ({ contest }: any) => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb pt-disabled' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/contest'>
                Contest
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                { contest.name }
            </a>
        </li>
    </ul>
);

const ContestChoices = ({ contest }: any) => {
    const choiceItems = _.map(contest.choices, (c: any) => (
        <li key={ c.id }>{ c.name }</li>
    ));

    return (
        <div>
            <h4>Choices:</h4>
            <ul>{ choiceItems }</ul>
        </div>
    );
};

const ContestDetailPage = (props: any) => {
    const { contest } = props;

    const row = (k: any, v: any) => (
        <tr>
            <td>{ k }</td>
            <td>{ v }</td>
        </tr>
    );

    return (
        <div>
            <Nav />
            <Breadcrumb contest={ contest } />
            <h2>Status</h2>
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
            <ContestChoices contest={ contest } />
        </div>
    );
};


export default ContestDetailPage;
