import * as React from 'react';
import { connect } from 'react-redux';


class Seed extends React.Component<any, any> {
    public render() {
        return <div>Seed</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Seed);
