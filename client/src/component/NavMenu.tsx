import * as React from 'react';

import { Menu, MenuDivider, MenuItem } from '@blueprintjs/core';
import { Link } from 'react-router-dom';


interface NavItemDef {
    path: string;
    iconName: string;
    text: string;
}

const NavItem = (def: NavItemDef): any => (
    <Link to={ def.path }>
        <MenuItem iconName={ def.iconName } text={ def.text } />
    </Link>
);

export default class NavMenu extends React.Component<any, any> {
    public render() {
        return (
            <Menu>
                <NavItem
                    text='Home'
                    path='/'
                    iconName='new-text-box'
                />
                <MenuDivider />
                <NavItem
                    text='Audit'
                    path='/audit'
                    iconName='new-text-box'
                />
                <NavItem
                    text='Report'
                    path='/audit/report'
                    iconName='new-text-box'
                />
                <NavItem
                    text='Round'
                    path='/audit/round'
                    iconName='new-text-box'
                />
                <NavItem
                    text='Seed'
                    path='/audit/seed'
                    iconName='new-text-box'
                />
                <NavItem
                    text='Upload'
                    path='/audit/upload'
                    iconName='new-text-box'
                />
            </Menu>
        );
    }
}
