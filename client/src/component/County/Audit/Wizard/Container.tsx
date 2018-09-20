import * as React from 'react';
import { connect } from 'react-redux';

import CountyAuditWizard from './Wizard';

import currentBallotNumber from 'corla/selector/county/currentBallotNumber';
import totalBallotsForBoard from 'corla/selector/county/totalBallotsForBoard';


interface WizardContainerProps {
    countyState: County.AppState;
    currentBallotNumber: number;
    totalBallotsForBoard: number;
}

class CountyAuditWizardContainer extends React.Component<WizardContainerProps> {
    public render() {
        const {
          currentBallotNumber,
          totalBallotsForBoard,
        } = this.props;

        return <CountyAuditWizard currentBallotNumber={ currentBallotNumber }
                                  totalBallotsForBoard={ totalBallotsForBoard }
                                  { ...this.props } />;
    }
}

function select(countyState: County.AppState) {
    return {
      countyState,
      currentBallotNumber: currentBallotNumber(countyState),
      totalBallotsForBoard: currentBallotNumber(countyState),
    };
}


export default connect(select)(CountyAuditWizardContainer);
