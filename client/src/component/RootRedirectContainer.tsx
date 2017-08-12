import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';


interface RootRedirectContainerProps {
    dashboard: any;
}

export class RootRedirectContainer extends React.Component<RootRedirectContainerProps & any, any> {
    public render() {
        const { dashboard } = this.props;

        if (dashboard === 'sos') {
            return <Redirect to='/sos' />;
        }

        return <Redirect to='/county' />;
    }
}

const mapStateToProps = ({ dashboard }: any) => ({ dashboard });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(RootRedirectContainer);
