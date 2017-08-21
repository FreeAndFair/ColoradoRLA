import * as React from 'react';
import { connect } from 'react-redux';

import counties from '../../../data/counties';

import CountyHomePage from './CountyHomePage';


class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const {
            county,
            history,
        } = this.props;

        const countyInfo = county.id ? counties[county.id] : {};
        const startAudit = () => history.push('/county/audit');

        const props = { countyInfo, startAudit, ...this.props };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    const { contestDefs } = county;

    return { contests: contestDefs, county };
};

export default connect(mapStateToProps)(CountyHomeContainer);
