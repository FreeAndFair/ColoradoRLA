import * as React from 'react';
import { connect } from 'react-redux';


class AuditRoundContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Audit Round</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(AuditRoundContainer);
