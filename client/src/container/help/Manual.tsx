import * as React from 'react';
import { connect } from 'react-redux';


class ManualContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Manual</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ManualContainer);
