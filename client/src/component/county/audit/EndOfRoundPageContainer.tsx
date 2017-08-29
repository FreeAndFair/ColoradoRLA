import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundPage from './EndOfRoundPage';

import countyInfo from '../../../selector/county/countyInfo';


class EndOfRoundPageContainer extends React.Component<any, any> {
    public render() {
        const { countyInfo } = this.props;

        const props = {
            countyInfo,
        };

        return <EndOfRoundPage { ...props } />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        countyInfo: countyInfo(state),
    };
};


export default connect(mapStateToProps)(EndOfRoundPageContainer);
