import * as React from 'react';
import { connect } from 'react-redux';


class ContestOverviewContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Contest Overview</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(ContestOverviewContainer);
