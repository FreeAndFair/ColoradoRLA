import * as React from 'react';
import { connect } from 'react-redux';

import EndOfLastRoundPage from './EndOfLastRoundPage';
import EndOfRoundPage from './EndOfRoundPage';

import allRoundsComplete from '../../../selector/county/allRoundsComplete';
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
        const { allRoundsComplete, countyInfo, previousRound } = this.props;

        if (allRoundsComplete) {
            return (
                <EndOfLastRoundPage
                    countyInfo={ countyInfo }
                    previousRound={ previousRound }/>
            );
        }

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
        allRoundsComplete: allRoundsComplete(state),
        countyInfo: countyInfo(state),
        previousRound: previousRound(state),
    };
};


export default connect(mapStateToProps)(EndOfRoundPageContainer);
