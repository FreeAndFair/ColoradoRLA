import * as React from 'react';


const MenuButton = () =>
    <button className='pt-button pt-minimal pt-icon-menu' />;

const Heading = () =>
    <div className='pt-navbar-heading'>Colorado RLA</div>;

const Divider = () =>
    <span className='pt-navbar-divider' />;

const HomeButton = () =>
    <button className='pt-button pt-minimal pt-icon-home'>Home</button>;

const UserButton = () =>
    <button className='pt-button pt-minimal pt-icon-user' />;

const SettingsButton = () =>
    <button className='pt-button pt-minimal pt-icon-cog' />;

export default class Nav extends React.Component<any, any> {
    public render() {
        return (
            <nav className='pt-navbar'>
                <div className='pt-navbar-group pt-align-left'>
                    <MenuButton />
                    <Heading />
                </div>
                <div className='pt-navbar-group pt-align-right'>
                    <HomeButton />
                    <Divider />
                    <UserButton />
                    <SettingsButton />
                </div>
            </nav>
        );
    }
}
