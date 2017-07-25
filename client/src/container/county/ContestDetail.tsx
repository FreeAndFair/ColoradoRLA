import * as React from 'react';
import { connect } from 'react-redux';


class CountyContestDetailContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Contest Detail</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyContestDetailContainer);
