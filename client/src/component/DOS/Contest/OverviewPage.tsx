import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import counties from 'corla/data/counties';

import Nav from '../Nav';


const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <Link to='/sos'>
                <div className='pt-breadcrumb'>
                    SoS
                </div>
            </Link>
        </li>
        <li>
            <div className='pt-breadcrumb pt-breadcrumb-current'>
                Contest
            </div>
        </li>
    </ul>
);

interface RowProps {
    contest: Contest;
}

const ContestTableRow = (props: RowProps) => {
    const { contest } = props;

    const county = counties[contest.countyId];

    return (
        <tr>
            <td>{ county.name }</td>
            <td>
                <Link to={ `/sos/contest/${contest.id}` }>
                    { contest.name }
                </Link>
            </td>
            <td>{ contest.choices.length }</td>
            <td>{ contest.votesAllowed }</td>
        </tr>
    );
};


interface TableProps {
    contests: DOS.Contests;
}

const ContestTable = (props: TableProps) => {
    const { contests } = props;

    const contestRows = _.map(contests, c => (
        <ContestTableRow key={ c.id } contest={ c } />
    ));

    return (
        <table className='pt-table pt-bordered pt-condensed'>
            <thead>
                <tr>
                    <th>County</th>
                    <th>Name</th>
                    <th>Choices</th>
                    <th>Vote For</th>
                </tr>
            </thead>
            <tbody>
                { contestRows }
            </tbody>
        </table>
    );
};

interface PageProps {
    contests: DOS.Contests;
}

const ContestOverviewPage = (props: PageProps) => {
    const { contests } = props;

    if (!contests) {
        return <div />;
    }

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <ContestTable contests={ contests } />
        </div>
    );
};


export default ContestOverviewPage;
