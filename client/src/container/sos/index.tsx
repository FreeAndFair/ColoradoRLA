import * as React from 'react';
import { connect } from 'react-redux';

import SoSRootPage from '../../component/sos';


class SoSRootContainer extends React.Component<any, any> {
    public render() {
        return <SoSRootPage />;
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(SoSRootContainer);
