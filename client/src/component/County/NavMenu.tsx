import * as React from 'react';

import { Link } from 'react-router-dom';


interface MenuItemDef {
    icon: string;
    path: string;
    text: string;
}

const MenuItem = (def: MenuItemDef) => {
    const className = `pt-menu-item ${def.icon}`;

    return (
        <Link to={ def.path }>
            <li>
                <span className={ className }>
                    { def.text }
                </span>
            </li>
        </Link>
    );
};

export default class CountyNavMenu extends React.Component {
    public render() {
        return (
            <ul className='pt-menu pt-election-1'>
                <MenuItem
                    text='Home'
                    path='/county'
                    icon='pt-icon-home'
                />
                <li className='pt-menu-divider' />
                <MenuItem
                    text='Audit Board'
                    path='/county/board'
                    icon='pt-icon-people'
                />
                <MenuItem
                    text='Audit'
                    path='/county/audit'
                    icon='pt-icon-eye-open'
                />
            </ul>
        );
    }
}
