import * as React from 'react';
import { connect } from 'react-redux';


class SoSRootContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>SoS</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(SoSRootContainer);
