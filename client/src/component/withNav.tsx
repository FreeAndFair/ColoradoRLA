import * as React from 'react';

import { Popover, Position } from '@blueprintjs/core';
import { Link } from 'react-router-dom';

import NavMenu from './NavMenu';


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


export default function withNav(Menu: any, path: any): any {
    return () => (
        <nav className='pt-navbar'>
            <div className='pt-navbar-group pt-align-left'>
                <Popover content={ <Menu /> } position={ Position.RIGHT_TOP }>
                    <MenuButton />
                </Popover>
                <Heading />
            </div>
            <div className='pt-navbar-group pt-align-right'>
                <HomeButton path={ path } />
                <Divider />
                <UserButton />
                <SettingsButton />
            </div>
        </nav>
    );
}
