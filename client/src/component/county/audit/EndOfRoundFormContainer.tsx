import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundForm from './EndOfRoundForm';

import countyInfo from '../../../selector/county/countyInfo';


class EndOfRoundFormContainer extends React.Component<any, any> {
    public render() {
        const { countyInfo } = this.props;

        const props = {
            countyInfo,
        };

        return <EndOfRoundForm {...props} />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        countyInfo: countyInfo(state),
    };
};


export default connect(mapStateToProps)(EndOfRoundFormContainer);
