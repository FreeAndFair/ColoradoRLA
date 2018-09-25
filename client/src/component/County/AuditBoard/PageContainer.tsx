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
    auditBoards: AuditBoards;
    countyName: string;
    countyState: County.AppState;
    hasAuditedAnyBallot: boolean;
    history: History;
    match: any;
}

class AuditBoardSignInContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            auditBoards,
            countyName,
            countyState,
            hasAuditedAnyBallot,
            history,
            match,
        } = this.props;

        const boardIndex = parseInt(match.params.id, 10);

        const auditBoardSignedIn = auditBoardSignedInSelector(
            boardIndex,
            countyState,
        );

        if (auditBoardSignedIn) {
            const auditBoardStartOrContinue = () =>
                history.push('/county/audit/' + boardIndex);

            return (
                <SignedInPage auditBoardStatus={ auditBoards[boardIndex] }
                              auditBoardIndex={ boardIndex }
                              auditBoardStartOrContinue={ auditBoardStartOrContinue }
                              countyName={ countyName }
                              hasAuditedAnyBallot={ hasAuditedAnyBallot } />
            );
        }

        return <AuditBoardPage auditBoardIndex={ boardIndex }
                               countyName={ countyName } />;
    }
}

interface SelectProps {
    auditBoards: AuditBoards;
    countyName: string;
    countyState: County.AppState;
    hasAuditedAnyBallot: boolean;
}

function select(countyState: County.AppState): SelectProps {
    const countyInfo = countyInfoSelector(countyState);
    const countyName = countyInfo!.name || '';

    return {
        auditBoards: countyState.auditBoards,
        countyName,
        countyState,
        hasAuditedAnyBallot: hasAuditedAnyBallotSelector(countyState),
    };
}


export default withState('County', withSync(
    AuditBoardSignInContainer,
    'COUNTY_BOARD_SIGN_IN_SYNC',
    select,
));
