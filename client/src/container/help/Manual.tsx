import * as React from 'react';
import { connect } from 'react-redux';


class ManualContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Manual</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ManualContainer);
