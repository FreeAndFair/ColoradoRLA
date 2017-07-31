import * as React from 'react';

import * as _ from 'lodash';

import Nav from '../Nav';


const ContestTableRow = ({ contest }: any) => (
    <tr>
        <td>{ contest.id }</td>
        <td>{ contest.name }</td>
        <td>{ contest.choices.length }</td>
        <td>{ contest.votesAllowed }</td>
    </tr>
);

const ContestTable = ({ contests }: any) => {
    const contestRows = _.map(contests, (c: any) => (
        <ContestTableRow key={ c.id } contest={ c } />
    ));

    return (
        <table>
            <thead>
                <tr>
                    <td>ID</td>
                    <td>Name</td>
                    <td>Choices</td>
                    <td>Vote For</td>
                </tr>
            </thead>
            <tbody>
                { contestRows }
            </tbody>
        </table>
    );
};

const ContestOverviewPage = (props: any) => {
    const { contests } = props;

    return (
        <div>
            <Nav />
            <ContestTable contests={ contests } />
        </div>
    );
};


export default ContestOverviewPage;
