import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundForm from './EndOfRoundForm';

import countyInfo from '../../../selector/county/countyInfo';


class EndOfRoundFormContainer extends React.Component<any, any> {
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
            onFirstNameChange: this.onFirstNameChange,
            onLastNameChange: this.onLastNameChange,
            onTextConfirm: this.onTextConfirm,
        };

        return <EndOfRoundForm {...props} />;
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
}

const mapStateToProps = (state: any) => {
    return {
        countyInfo: countyInfo(state),
    };
};


export default connect(mapStateToProps)(EndOfRoundFormContainer);
