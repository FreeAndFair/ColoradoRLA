import * as React from 'react';

import * as _ from 'lodash';

import { formatCountyASMState } from 'corla/format';

import FileDownloadButtons from 'corla/component/FileDownloadButtons';

import Nav from '../Nav';


interface BreadcrumbProps {
    county: CountyInfo;
}

const Breadcrumb = ({ county }: BreadcrumbProps) => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb pt-disabled' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/county'>
                County
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                { county.name }
            </a>
        </li>
    </ul>
);

function formatMember(member: AuditBoardMember): string {
    const { firstName, lastName, party } = member;

    return `${firstName} ${lastName} (${party})`;
}

interface AuditBoardProps {
    auditBoard: AuditBoardStatus;
}

const AuditBoard = (props: AuditBoardProps) => {
    const { auditBoard } = props;

    return (
        <div className='pt-card'>
            <h3>Audit Board</h3>
            <table className='pt-table pt-bordered pt-condensed'>
                <tbody>
                    <tr>
                        <td><strong>Board Member #1:</strong></td>
                        <td>{ formatMember(auditBoard.members[0]) }</td>
                    </tr>
                    <tr>
                        <td><strong>Board Member #2:</strong></td>
                        <td>{ formatMember(auditBoard.members[1]) }</td>
                    </tr>
                    <tr>
                        <td><strong>Sign-in Time:</strong></td>
                        <td>{ `${auditBoard.signIn}` }</td>
                    </tr>
                </tbody>
            </table>
        </div>
    );
};

const NoAuditBoard = () => {
    return (
        <div className='pt-card'>
            <h3>Audit Board</h3>
            <div className='pt-card'>
                Audit Board not signed in.
            </div>
        </div>
    );
};

interface DetailsProps {
    county: CountyInfo;
    status: DOS.CountyStatus;
}

const CountyDetails = (props: DetailsProps) => {
    const { county, status } = props;
    const { auditBoard } = status;

    const countyState = formatCountyASMState(status.asmState);
    const submitted = status.auditedBallotCount;

    const auditedCount = _.get(status, 'discrepancyCount.audited') || '—';
    const unauditedCount = _.get(status, 'discrepancyCount.unaudited') || '—';


    const auditBoardSection = auditBoard
                            ? <AuditBoard auditBoard={ auditBoard } />
                            : <NoAuditBoard />;

    return (
        <div className='pt-card'>
            <div className='pt-card'>
                <h3>County Info</h3>
                <table className='pt-table pt-bordered pt-condensed'>
                    <tbody>
                        <tr>
                            <td><strong>Name:</strong></td>
                            <td>{ county.name }</td>
                        </tr>
                        <tr>
                            <td><strong>Status:</strong></td>
                            <td>{ countyState }</td>
                        </tr>
                        <tr>
                            <td><strong>Ballots Submitted:</strong></td>
                            <td>{ submitted }</td>
                        </tr>
                        <tr>
                            <td><strong>Audited Contest Discrepancies:</strong></td>
                            <td>{ auditedCount }</td>
                        </tr>
                        <tr>
                            <td><strong>Non-audited Contest Discrepancies:</strong></td>
                            <td>{ unauditedCount }</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <FileDownloadButtons status={ status } />
            { auditBoardSection }
        </div>
    );
};

interface PageProps {
    county: CountyInfo;
    status: DOS.CountyStatus;
}

const CountyDetailPage = (props: PageProps) => {
    const { county, status } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb county={ county } />
            <h3>{ county.name } Name</h3>
            <CountyDetails county={ county } status={ status } />
        </div>
    );
};


export default CountyDetailPage;
