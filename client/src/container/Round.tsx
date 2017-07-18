import * as React from 'react';
import { connect } from 'react-redux';


class Round extends React.Component<any, any> {
    public render() {
        return <div>Round</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Round);
