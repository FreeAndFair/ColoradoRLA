import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import LoginForm, { FormFields } from './LoginForm';

import authCountyAdmin from '../../action/authCountyAdmin';
import authStateAdmin from '../../action/authStateAdmin';
import dosDashboardRefresh from '../../action/dosDashboardRefresh';
import fetchContests from '../../action/fetchContests';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const { submit } = this.props;

        return <LoginForm submit={ submit } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
    const b = bindActionCreators({
        authCountyAdmin,
        authStateAdmin,
        dosDashboardRefresh,
        fetchContests,
    }, dispatch);

    const submit = ({ dashboard, username, password }: any) => {
        switch (dashboard) {
            case 'sos': {
                b.authStateAdmin(username, password);
                b.dosDashboardRefresh();
                b.fetchContests();
                break;
            }
            case 'county': {
                b.authCountyAdmin(username, password);
                break;
            }
        }
    };

    return { submit };
};

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(LoginFormContainer);
