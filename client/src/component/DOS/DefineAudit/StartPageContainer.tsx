import * as React from 'react';

import { History } from 'history';

import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import StartPage from './StartPage';

import withSync from 'corla/component/withSync';


interface ContainerProps {
    election: Election;
    history: History;
    publicMeetingDate: Date;
    riskLimit: number;
    sos: DOS.AppState;
}

interface ContainerState {
    riskLimit: boolean;
    type: boolean;
}

class StartPageContainer extends React.Component<ContainerProps, ContainerState> {
    public state = {
        riskLimit: true,
        type: false,
    };

    public render() {
        const { election, history, publicMeetingDate, riskLimit, sos } = this.props;

        if (!sos) {
            return <div />;
        }

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            election,
            formValid: this.formIsValid(),
            nextPage: () => history.push('/sos/audit/select-contests'),
            publicMeetingDate,
            riskLimit,
            setFormValid: this.setFormValid,
        };

        return <StartPage { ...props } />;
    }

    private formIsValid = () => {
        return this.state.riskLimit && this.state.type;
    }

    private setFormValid = (s: any) => {
        this.setState(s);
    }
}

function select(state: AppState) {
    const { sos } = state;

    if (!sos) { return {}; }

    const { election, publicMeetingDate, riskLimit } = sos;

    return { election, riskLimit, publicMeetingDate, sos };
}


export default withSync(
    StartPageContainer,
    'DOS_DEFINE_AUDIT_SYNC',
    select,
);
