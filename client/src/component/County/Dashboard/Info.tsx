import * as React from 'react';

import { formatCountyAsmState } from 'corla/format';

import * as _ from 'lodash';


interface ContestInfoTableRowProps {
    choice: ContestChoice;
}

const ContestInfoTableRow = (props: ContestInfoTableRowProps) => {
    const { choice } = props;
    return (
        <tr>
            <td>{ choice.name }</td>
            <td>{ choice.description }</td>
        </tr>
    );
};

interface ContestInfoTableProps {
    contest: Contest;
}

const ContestInfoTable = (props: ContestInfoTableProps) => {
    const { contest } = props;
    const body = _.map(contest.choices, c => {
        return <ContestInfoTableRow key={ c.name } choice={ c } />;
    });

    return (
        <div className='pt-card'>
            <span>{ contest.name }</span>
            <table className='pt-table rla-county-contest-info'>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    { body }
                </tbody>
            </table>
        </div>
    );
};

interface ContestInfoProps {
    contests: CountyContests;
}

const ContestInfo = (props: ContestInfoProps) => {
    const { contests } = props;

    const contestTables = _.map(contests, c => {
        if (!c) {
            return <div />;
        }

        return <ContestInfoTable key={ c.name } contest={ c } />;
    });

    return (
        <div className='contest-info pt-card'>
            <h3>Contest Info</h3>
            <div>
                { contestTables }
            </div>
        </div>
    );
};

interface CountyInfoProps {
    county: CountyState;
    currentRoundNumber: number;
    info: CountyInfo;
}

const CountyInfo = (props: CountyInfoProps) => {
    const { county, currentRoundNumber, info } = props;

    const rows = [
        ['County:', info.name],
        ['Status:', formatCountyAsmState(county.asm.county.currentState)],
        ['Current Round:', currentRoundNumber],
        ['Ballot cards remaining in round:', county.ballotsRemainingInRound],
        ['Ballot cards audited (all rounds):', county.auditedBallotCount],
        ['Disagreements (all rounds):', county.disagreementCount],
        ['Discrepancies (all rounds):', county.discrepancyCount],
    ].map(([k, v]) => (
        <tr key={ k }>
            <td><strong>{ k }</strong></td>
            <td>{ v }</td>
        </tr>
    ));

    return (
        <div className='county-info pt-card'>
            <h3>County Info</h3>
            <div className='pt-card'>
                <table className='pt-table pt-condensed'>
                    <tbody>{ rows }</tbody>
                </table>
            </div>
        </div>
    );
};


interface InfoProps {
    info: CountyInfo;
    contests: CountyContests;
    county: CountyState;
    currentRoundNumber: number;
}

const Info = (props: InfoProps) => {
    const { info, contests, county, currentRoundNumber } = props;

    return (
        <div className='info pt-card'>
            <CountyInfo county={ county }
                        info={ info }
                        currentRoundNumber={ currentRoundNumber } />
            <ContestInfo contests={ contests } />
        </div>
    );
};


export default Info;
