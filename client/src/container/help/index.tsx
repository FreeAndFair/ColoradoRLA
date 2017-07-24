import * as React from 'react';
import { connect } from 'react-redux';


class HelpRootContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Help</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(HelpRootContainer);
