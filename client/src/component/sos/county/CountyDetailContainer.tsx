import * as React from 'react';
import { connect } from 'react-redux';

import CountyDetailPage from './CountyDetailPage';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        const { countyId } = this.props.match.params;
        const county = this.props.counties[countyId];

        return <CountyDetailPage county={ county } />;
    }
}

const mapStateToProps = (state: any) => ({
    counties: state.sos.counties,
});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyDetailContainer);
