import * as React from 'react';
import { connect } from 'react-redux';

import { Hello } from '../component/Hello';


export interface HomeProps {
    greeting: string;
}

class App extends React.Component<HomeProps & any, any> {
    public render() {
        const { greeting, onClick } = this.props;

        return <Hello onClick={ onClick } greeting={ greeting } />;
    }
}

const mapStateToProps = ({ greeting }: any) => ({ greeting });

const mapDispatchToProps = (dispatch: any) => ({
    onClick() {
        dispatch({ type: 'NEXT_GREETING' });
    },
});

export default connect(mapStateToProps, mapDispatchToProps)(App);
