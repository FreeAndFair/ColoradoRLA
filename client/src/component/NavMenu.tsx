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
                    iconName='pt-icon-home'
                />
                <MenuDivider />
                <NavItem
                    text='Audit'
                    path='/audit'
                    iconName='pt-icon-eye-open'
                />
                <NavItem
                    text='Report'
                    path='/audit/report'
                    iconName='pt-icon-timeline-bar-chart'
                />
                <NavItem
                    text='Round'
                    path='/audit/round'
                    iconName='pt-icon-repeat'
                />
                <NavItem
                    text='Seed'
                    path='/audit/seed'
                    iconName='pt-icon-numerical'
                />
                <NavItem
                    text='Upload'
                    path='/audit/upload'
                    iconName='pt-icon-folder-close'
                />
            </Menu>
        );
    }
}
