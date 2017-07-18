import * as React from 'react';
import { connect } from 'react-redux';


class Report extends React.Component<any, any> {
    public render() {
        return <div>Report</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Report);
