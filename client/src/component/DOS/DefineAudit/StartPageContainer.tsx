import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import StartPage from './StartPage';


class StartPageContainer extends React.Component<any, any> {
    public state: any = {
        formValid: false,
    };

    public render() {
        const { election, history, publicMeetingDate, riskLimit, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            election,
            formValid: this.state.formValid,
            nextPage: () => history.push('/sos/audit/select-contests'),
            publicMeetingDate,
            riskLimit,
            setFormValid: this.setFormValid,
        };

        return <StartPage { ...props } />;
    }

    private setFormValid = (formValid: boolean) => {
        this.setState({ formValid });
    }
}


const mapStateToProps = ({ sos }: any) => {
    const { election, publicMeetingDate, riskLimit } = sos;

    return { election, riskLimit, publicMeetingDate, sos };
};

export default connect(mapStateToProps)(StartPageContainer);
