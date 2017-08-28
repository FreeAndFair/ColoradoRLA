import * as React from 'react';
import { connect } from 'react-redux';

import SignedInPage from './AuditBoard/SignedInPage';
import SignInPage from './AuditBoard/SignInPage';

import auditBoardSignedIn from '../../selector/county/auditBoardSignedIn';


class AuditBoardSignInContainer extends React.Component<any, any> {
    public render() {
        const { auditBoard, auditBoardSignedIn } = this.props;

        if (auditBoardSignedIn) {
            return <SignedInPage auditBoard={ auditBoard } />;
        }

        return <SignInPage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    return {
        auditBoard: county.auditBoard,
        auditBoardSignedIn: auditBoardSignedIn(state),
        county,
    };
};


export default connect(mapStateToProps)(AuditBoardSignInContainer);
