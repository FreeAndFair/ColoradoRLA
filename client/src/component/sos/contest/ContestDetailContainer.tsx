import * as React from 'react';
import { connect } from 'react-redux';

import ContestDetailPage from './ContestDetailPage';


class ContestDetailContainer extends React.Component<any, any> {
    public render() {
        return <ContestDetailPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ContestDetailContainer);
