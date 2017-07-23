import * as React from 'react';
import { connect } from 'react-redux';


class GlossaryContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>Glossary</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(GlossaryContainer);
