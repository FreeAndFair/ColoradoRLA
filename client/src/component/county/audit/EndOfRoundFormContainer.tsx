import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundForm from './EndOfRoundForm';


class EndOfRoundFormContainer extends React.Component<any, any> {
    public render() {
        return <EndOfRoundForm />;
    }
}

const mapStateToProps = (state: any) => ({});


export default connect(mapStateToProps)(EndOfRoundFormContainer);
