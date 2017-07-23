import * as React from 'react';
import { connect } from 'react-redux';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Audit</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyAuditContainer);
