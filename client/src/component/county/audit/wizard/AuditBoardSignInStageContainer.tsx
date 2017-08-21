import * as React from 'react';
import { connect } from 'react-redux';

import AuditBoardSignInStage from './AuditBoardSignInStage';


class AuditBoardSignInStageContainer extends React.Component<any, any> {
    public render() {
        return <AuditBoardSignInStage { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    return {
        auditBoard: county.auditBoardMembers,
        county,
    };
};


export default connect(mapStateToProps)(AuditBoardSignInStageContainer);
