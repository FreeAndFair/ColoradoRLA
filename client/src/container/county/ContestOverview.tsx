import * as React from 'react';
import { connect } from 'react-redux';


class CountyContestOverviewContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Contest Overview</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyContestOverviewContainer);
