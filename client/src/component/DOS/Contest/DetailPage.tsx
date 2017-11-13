import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import counties from 'corla/data/counties';

import Nav from '../Nav';


interface BreadcrumbProps {
    contest: Contest;
}

const Breadcrumb = ({ contest }: BreadcrumbProps) => (
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

interface ChoicesProps {
    contest: Contest;
}

const ContestChoices = (props: ChoicesProps) => {
    const { contest } = props;

    const choiceItems = _.map(contest.choices, (c, k) => (
        <li key={ k }>{ c.name }</li>
    ));

    return (
        <div className='pt-card'>
            <h4>Choices:</h4>
            <ul>{ choiceItems }</ul>
        </div>
    );
};

interface PageProps {
    contest: Contest;
}

const ContestDetailPage = (props: PageProps) => {
    const { contest } = props;

    const row = (k: string, v: (number | string)) => (
        <tr key={ k } >
            <td><strong>{ k }</strong></td>
            <td>{ v }</td>
        </tr>
    );

    const county = counties[contest.countyId];

    return (
        <div>
            <Nav />
            <Breadcrumb contest={ contest } />
            <h2>Status</h2>
            <div className='pt-card'>
                <h3>Contest Data</h3>
                <table className='pt-table pt-bordered pt-condensed'>
                    <tbody>
                        { row('County', county.name) }
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
