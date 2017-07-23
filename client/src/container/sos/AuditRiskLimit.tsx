import * as React from 'react';
import { connect } from 'react-redux';


class AuditRiskLimitContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Audit Risk Limit</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(AuditRiskLimitContainer);
