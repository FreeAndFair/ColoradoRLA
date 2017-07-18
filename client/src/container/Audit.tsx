import * as React from 'react';
import { connect } from 'react-redux';


class Audit extends React.Component<any, any> {
    public render() {
        return <div>Audit</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Audit);
