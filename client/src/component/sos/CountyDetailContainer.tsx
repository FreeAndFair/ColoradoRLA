import * as React from 'react';
import { connect } from 'react-redux';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Detail</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyDetailContainer);
