import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { Redirect } from 'react-router-dom';

import session from 'corla/session';

import LoginPage from './Page';


interface LoginProps extends RouteComponentProps<void> {
    stateType: AppStateType;
}

export class LoginContainer extends React.Component<LoginProps> {
    public render() {
        const { stateType } = this.props;

        const s = session.get();

        if (s) {
            const { type } = s;

            if (type === 'county' && stateType === 'County') {
                return <Redirect to='/county' />;
            }

            if (type === 'dos' && stateType === 'DOS') {
                return <Redirect to='/sos' />;
            }

            session.expire();
        }

        return <LoginPage />;
    }
}

function select(state: AppState) {
    return { stateType: state.type };
}


export default connect(select)(LoginContainer);
