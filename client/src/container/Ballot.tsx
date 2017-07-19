import * as React from 'react';
import { connect } from 'react-redux';

import Nav from '../component/Nav';


class Ballot extends React.Component<any, any> {
    public render() {
        return (
            <div>
                <Nav />
                <div>Ballot</div>
            </div>
        );
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Ballot);
