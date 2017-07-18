import * as React from 'react';
import { connect } from 'react-redux';


class Login extends React.Component<any, any> {
    public render() {
        return <div>Login</div>;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Login);
