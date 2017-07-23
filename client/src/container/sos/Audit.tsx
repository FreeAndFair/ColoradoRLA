import * as React from 'react';
import { connect } from 'react-redux';


class AuditContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Audit</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(AuditContainer);
