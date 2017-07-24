import * as React from 'react';
import { connect } from 'react-redux';


class GlossaryContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Glossary</div>
        );
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(GlossaryContainer);
