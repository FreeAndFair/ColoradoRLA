import * as React from 'react';

import { History } from 'history';

import withState from 'corla/component/withState';
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

interface SelectProps {
    auditBoard: AuditBoard;
    auditBoardSignedIn: boolean;
    countyName: string;
    hasAuditedAnyBallot: boolean;
}

function select(countyState: County.AppState): SelectProps {
    const countyInfo = countyInfoSelector(countyState);
    const countyName = countyInfo!.name || '';

    return {
        auditBoard: countyState.auditBoard,
        auditBoardSignedIn: auditBoardSignedInSelector(countyState),
        countyName,
        hasAuditedAnyBallot: hasAuditedAnyBallotSelector(countyState),
    };
}


export default withState('County', withSync(
    AuditBoardSignInContainer,
    'COUNTY_BOARD_SIGN_IN_SYNC',
    select,
));
