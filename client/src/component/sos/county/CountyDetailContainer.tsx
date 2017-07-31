import * as React from 'react';
import { connect } from 'react-redux';

import CountyDetailPage from './CountyDetailPage';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        const county = {
            id: 123,
            name: 'Acme County',
        };

        return <CountyDetailPage county={ county } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(CountyDetailContainer);
