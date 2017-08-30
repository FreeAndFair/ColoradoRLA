import * as React from 'react';
import { connect } from 'react-redux';

import EndOfRoundPage from './EndOfRoundPage';

import countyInfo from '../../../selector/county/countyInfo';
import previousRound from '../../../selector/county/previousRound';


function signedOff(round: any): boolean {
    if (!round.signatories) {
        return false;
    }

    if (round.signatories.length < 2) {
        return false;
    }

    return true;
}

class EndOfRoundPageContainer extends React.Component<any, any> {
    public render() {
        const { countyInfo, previousRound } = this.props;

        const props = {
            countyInfo,
            previousRound,
            previousRoundSignedOff: signedOff(previousRound),
        };

        return <EndOfRoundPage { ...props } />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        countyInfo: countyInfo(state),
        previousRound: previousRound(state),
    };
};


export default connect(mapStateToProps)(EndOfRoundPageContainer);
