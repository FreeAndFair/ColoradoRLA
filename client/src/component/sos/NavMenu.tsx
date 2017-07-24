import * as React from 'react';

import { Link } from 'react-router-dom';


interface MenuItemDef {
    icon: string;
    path: string;
    text: string;
}

const MenuItem = (def: MenuItemDef): any => {
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

export default class SoSNavMenu extends React.Component<any, any> {
    public render() {
        return (
            <ul className='pt-menu pt-election-1'>
                <MenuItem
                    text='Home'
                    path='/sos'
                    icon='pt-icon-home'
                />
                <li className='pt-menu-divider' />
                <MenuItem
                    text='Counties'
                    path='/sos/county'
                    icon='pt-icon-map'
                />
                <MenuItem
                    text='Contests'
                    path='/sos/contest'
                    icon='pt-icon-numbered-list'
                />
                <MenuItem
                    text='Audit'
                    path='/sos/audit'
                    icon='pt-icon-eye-open'
                />
            </ul>
        );
    }
}
