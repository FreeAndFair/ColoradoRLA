import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import AuditBoardSignInStage from './AuditBoardSignInStage';

import establishAuditBoard from '../../../../action/establishAuditBoard';


class AuditBoardSignInStageContainer extends React.Component<any, any> {
    public render() {
        return <AuditBoardSignInStage { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    return {
        auditBoard: county.auditBoardMembers, county
    };
};

const mapDispatchToProps = (dispatch: any) => bindActionCreators({
    establishAuditBoard,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditBoardSignInStageContainer);
