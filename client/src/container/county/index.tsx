import * as React from 'react';
import { connect } from 'react-redux';


class CountyRootContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyRootContainer);
