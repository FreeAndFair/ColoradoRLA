import * as React from 'react';
import { connect } from 'react-redux';

import counties from '../../../data/counties';

import CountyHomePage from './CountyHomePage';

import auditBoardSignedIn from '../../../selector/county/auditBoardSignedIn';
import canAudit from '../../../selector/county/canAudit';
import canSignIn from '../../../selector/county/canSignIn';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const {
            canAudit,
            canSignIn,
            county,
            history,
        } = this.props;

        const countyInfo = county.id ? counties[county.id] : {};
        const boardSignIn = () => history.push('/county/sign-in');
        const startAudit = () => history.push('/county/audit');

        const props = {
            boardSignIn,
            canAudit,
            canSignIn,
            countyInfo,
            startAudit,
            ...this.props,
        };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;
    const { contestDefs } = county;

    return {
        auditBoardSignedIn: auditBoardSignedIn(state),
        canAudit: canAudit(state),
        canSignIn: canSignIn(state),
        contests: contestDefs,
        county,
    };
};

export default connect(mapStateToProps)(CountyHomeContainer);
