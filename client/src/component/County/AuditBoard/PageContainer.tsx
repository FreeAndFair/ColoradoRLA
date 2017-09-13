import * as React from 'react';
import { connect } from 'react-redux';

import counties from 'corla/data/counties';

import AuditBoardPage from './Page';
import SignedInPage from './SignedInPage';

import auditBoardSignedInSelector from 'corla/selector/county/auditBoardSignedIn';
import countyInfoSelector from 'corla/selector/county/countyInfo';
import hasAuditedAnyBallotSelector from 'corla/selector/county/hasAuditedAnyBallot';


class AuditBoardSignInContainer extends React.Component<any, any> {
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

const mapStateToProps = (state: any) => {
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
};


export default connect(mapStateToProps)(AuditBoardSignInContainer);
