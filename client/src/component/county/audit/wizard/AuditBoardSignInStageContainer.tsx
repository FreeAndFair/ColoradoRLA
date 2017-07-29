import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import AuditBoardSignInStage from './AuditBoardSignInStage';


class AuditBoardSignInStageContainer extends React.Component<any, any> {
    public render() {
        return <AuditBoardSignInStage { ...this.props } />;
    }
}

const mapStateToProps = ({ county }: any) =>
    ({ auditBoard: county.auditBoard, county });

const mapDispatchToProps = (dispatch: any) => bindActionCreators({
    updateBoardMember: (index: any, name: any, party: any) => ({
        data: { index, name, party },
        type: 'UPDATE_BOARD_MEMBER',
    }),
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditBoardSignInStageContainer);
