import * as React from 'react';

import { History } from 'history';

import withSync from 'corla/component/withSync';

import counties from 'corla/data/counties';

import AuditBoardPage from './Page';
import SignedInPage from './SignedInPage';

import auditBoardSignedInSelector from 'corla/selector/county/auditBoardSignedIn';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import hasAuditedAnyBallotSelector from 'corla/selector/county/hasAuditedAnyBallot';


interface ContainerProps {
    auditBoard: AuditBoard;
    auditBoardSignedIn: boolean;
    countyName: string;
    hasAuditedAnyBallot: boolean;
    history: History;
}

class AuditBoardSignInContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            auditBoard,
            auditBoardSignedIn,
            countyName,
            hasAuditedAnyBallot,
            history,
        } = this.props;

        if (auditBoardSignedIn) {
            const auditBoardStartOrContinue = () =>
                history.push('/county/audit');

            return (
                <SignedInPage auditBoard={ auditBoard }
                              auditBoardStartOrContinue={ auditBoardStartOrContinue }
                              countyName={ countyName }
                              hasAuditedAnyBallot={ hasAuditedAnyBallot } />
            );
        }

        return <AuditBoardPage { ...this.props } />;
    }
}

function select(state: AppState) {
    const { county } = state;

    const countyInfo = countyInfoSelector(state);
    const countyName = countyInfo.name || '';

    return {
        auditBoard: county.auditBoard,
        auditBoardSignedIn: auditBoardSignedInSelector(state),
        county,
        countyName,
        hasAuditedAnyBallot: hasAuditedAnyBallotSelector(state),
    };
}


export default withSync(
    AuditBoardSignInContainer,
    'COUNTY_BOARD_SIGN_IN_SYNC',
    select,
);
