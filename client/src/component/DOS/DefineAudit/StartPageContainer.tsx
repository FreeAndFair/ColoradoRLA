import * as React from 'react';

import { History } from 'history';

import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import StartPage from './StartPage';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';


interface ContainerProps {
    election: Election;
    history: History;
    publicMeetingDate: Date;
    riskLimit: number;
    dosState: DOS.AppState;
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
        const { election, history, publicMeetingDate, riskLimit, dosState } = this.props;

        if (!dosState) {
            return <div />;
        }

        if (!dosState.asm) {
            return <div />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
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

    private setFormValid = (s: Pick<ContainerState, keyof ContainerState>) => {
        this.setState(s);
    }
}

function select(dosState: DOS.AppState) {
    const { election, publicMeetingDate, riskLimit } = dosState;

    return { election, riskLimit, publicMeetingDate, dosState };
}


export default withSync(
    withDOSState(StartPageContainer),
    'DOS_DEFINE_AUDIT_SYNC',
    select,
);
