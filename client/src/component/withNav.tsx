import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import { Popover, Position } from '@blueprintjs/core';
import { Link } from 'react-router-dom';

import NavMenu from './NavMenu';

import logout from '../action/logout';
import resetDatabase from '../action/resetDatabase';


const MenuButton = () =>
    <button className='pt-button pt-minimal pt-icon-menu' />;

const Heading = () =>
    <div className='pt-navbar-heading'>Colorado RLA</div>;

const Divider = () =>
    <span className='pt-navbar-divider' />;

const HomeButton = ({ path }: any) => (
    <Link to={ path }>
        <button className='pt-button pt-minimal pt-icon-home'>Home</button>
    </Link>
);

const UserButton = () =>
    <button className='pt-button pt-minimal pt-icon-user' />;

const SettingsButton = () =>
    <button className='pt-button pt-minimal pt-icon-cog' />;

const LogoutButton = ({ logout }: any) =>
    <button className='pt-button pt-minimal pt-icon-log-out' onClick={ logout } />;


const ResetDatabaseButton = ({ reset }: any) => (
    <button
        className='pt-button pt-intent-danger pt-icon-warning-sign'
        onClick={ reset }>
        DANGER: Reset Database
    </button>
);


export default function withNav(Menu: any, path: any): any {
    class Nav extends React.Component<any, any> {
        public render() {
            const resetSection = path === '/sos'
                               ? <ResetDatabaseButton reset={ this.props.resetDatabase } />
                               : <div />;

            return (
                <nav className='pt-navbar'>
                    <div className='pt-navbar-group pt-align-left'>
                        <Popover content={ <Menu /> } position={ Position.RIGHT_TOP }>
                            <MenuButton />
                        </Popover>
                        <Heading />
                    </div>
                    <div className='pt-navbar-group pt-align-right'>
                        { resetSection }
                        <Divider />
                        <HomeButton path={ path } />
                        <Divider />
                        <LogoutButton logout={ logout }/>
                    </div>

                </nav>
            );
        }
    }

    const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
        resetDatabase,
    }, dispatch);

    return connect(null, mapDispatchToProps)(Nav);
}
