import * as React from 'react';
import { connect } from 'react-redux';

import SignOffForm from './SignOffForm';

import roundSignOff from 'corla/action/county/roundSignOff';

import countyInfo from 'corla/selector/county/countyInfo';


interface ContainerProps {
    countyInfo: CountyInfo;
}

interface ContainerState {
    form: Elector[];
}

class SignOffFormContainer extends React.Component<ContainerProps, ContainerState> {
    public state = {
        form: [
            {
                firstName: '',
                lastName: '',
            },
            {
                firstName: '',
                lastName: '',
            },
        ],
    };

    public render() {
        const { countyInfo } = this.props;

        const props = {
            countyInfo,
            form: this.state.form,
            formIsValid: this.formIsValid(),
            onFirstNameChange: this.onFirstNameChange,
            onLastNameChange: this.onLastNameChange,
            onTextConfirm: this.onTextConfirm,
            submit: () => roundSignOff(this.state.form),
        };

        return <SignOffForm { ...props } />;
    }

    private onTextConfirm = () => {
        const s = { ...this.state };

        s.form[0].firstName = s.form[0].firstName.trim();
        s.form[0].lastName = s.form[0].lastName.trim();

        s.form[1].firstName = s.form[1].firstName.trim();
        s.form[1].lastName = s.form[1].lastName.trim();

        this.setState(s);
    }

    private onFirstNameChange = (index: number) => (name: string) => {
        const s = { ...this.state };

        s.form[index].firstName = name;

        this.setState(s);
    }

    private onLastNameChange = (index: number) => (name: string) => {
        const s = { ...this.state };

        s.form[index].lastName = name;

        this.setState(s);
    }

    private formIsValid = () => {
        const { form } = this.state;

        return !!(form[0].firstName
               && form[0].lastName
               && form[1].firstName
               && form[1].lastName);
    }
}

function select(countyState: County.AppState) {
    return {
        countyInfo: countyInfo(countyState),
    };
}


export default connect(select)(SignOffFormContainer);
