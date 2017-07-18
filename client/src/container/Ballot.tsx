import * as React from 'react';
import { connect } from 'react-redux';


class Ballot extends React.Component<any, any> {
    public render() {
        return <div>Ballot</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Ballot);
