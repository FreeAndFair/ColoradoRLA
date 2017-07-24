import * as React from 'react';
import { connect } from 'react-redux';


class CountyOverviewContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Overview</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyOverviewContainer);
