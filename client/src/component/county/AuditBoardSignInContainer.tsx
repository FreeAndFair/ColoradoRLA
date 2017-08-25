import * as React from 'react';
import { connect } from 'react-redux';

import AuditBoardSignInPage from './AuditBoardSignInPage';


class AuditBoardSignInContainer extends React.Component<any, any> {
    public render() {
        return <AuditBoardSignInPage { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    return {
        auditBoard: county.auditBoardMembers,
        county,
    };
};


export default connect(mapStateToProps)(AuditBoardSignInContainer);
