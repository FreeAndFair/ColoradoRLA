import * as React from 'react';
import { connect } from 'react-redux';

import TempUserLoginPage from './TempUserLoginPage';


export class TempUserLoginContainer extends React.Component<any, any> {
    public render() {
        const props = {
            loginAsCounty: () => this.props.history.push('/county'),
            loginAsSoS: () => this.props.history.push('/sos'),
        };

        return <TempUserLoginPage { ...props } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(TempUserLoginContainer);
