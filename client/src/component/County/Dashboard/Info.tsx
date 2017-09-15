import * as React from 'react';

import { formatCountyAsmState } from 'corla/format';

import * as _ from 'lodash';


const ContestInfoTableRow = ({ choice }: any) => (
    <tr>
        <td>{ choice.name }</td>
        <td>{ choice.description }</td>
    </tr>
);

const ContestInfoTable = ({ contest }: any) => {
    const body = _.map(contest.choices, (c: any) => {
        return <ContestInfoTableRow key={ c.name } choice={ c } />;
    });

    return (
        <div className='pt-card'>
            <span>{ contest.name }</span>
            <table className='pt-table'>
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

const ContestInfo = ({ contests }: any): any => {
    const contestTables = _.map(contests, (c: any) => {
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

const CountyInfo = ({ county, currentRoundNumber, info }: any) => {
    const { ballotsToAudit } = county;

    const rows = [
        ['County:', info.name],
        ['Status:', formatCountyAsmState(county.asm.county.currentState)],
        ['Current Round:', currentRoundNumber],
        ['# Ballots to audit:', county.ballotsRemainingInRound],
        ['# Ballots audited:', county.auditedBallotCount],
        ['# Disagreements:', county.disagreementCount],
        ['# Discrepancies:', county.discrepancyCount],
    ].map(([k, v]: any) => (
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

const Info = ({ info, contests, county, currentRoundNumber }: any) => (
    <div className='info pt-card'>
        <CountyInfo county={ county } info={ info } currentRoundNumber={ currentRoundNumber } />
        <ContestInfo contests={ contests } />
    </div>
);


export default Info;
