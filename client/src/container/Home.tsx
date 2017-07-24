import * as React from 'react';
import { connect } from 'react-redux';

import { Hello } from '../component/Hello';
import NavMenu from '../component/NavMenu';
import withNav from '../component/withNav';


const Nav = withNav(NavMenu);

export interface HomeContainerProps {
    greeting: string;
}

class HomeContainer extends React.Component<HomeContainerProps & any, any> {
    public render() {
        const { greeting, onClick } = this.props;

        return (
            <div>
                <Nav />
                <Hello onClick={ onClick } greeting={ greeting } />
            </div>
        );
    }
}

const mapStateToProps = ({ greeting }: any) => ({ greeting });

const mapDispatchToProps = (dispatch: any) => ({
    onClick() {
        dispatch({ type: 'NEXT_GREETING' });
    },
});

export default connect(mapStateToProps, mapDispatchToProps)(HomeContainer);
