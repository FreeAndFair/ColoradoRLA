import * as React from 'react';
import { connect } from 'react-redux';
import { Link, Redirect } from 'react-router-dom';

import * as _ from 'lodash';

import { Menu, MenuDivider, MenuItem } from '@blueprintjs/core';


class SoSNavMenu extends React.Component<any, any> {
    public render() {
        const { currentAsmState, sos } = this.props;

        const disableStates = [
            'AUDIT_READY_TO_START',
            'DOS_AUDIT_ONGOING',
            'DOS_AUDIT_COMPLETE',
            'AUDIT_RESULTS_PUBLISHED',
        ];
        const disableAuditButton = _.includes(disableStates, currentAsmState);

        return (
            <Menu>
                <Link to='/sos'>
                    <MenuItem
                        text='Home'
                        iconName='home'
                    />
                </Link>
                <MenuDivider />
                <Link to='/sos/county'>
                    <MenuItem
                        text='Counties'
                        iconName='map'
                    />
                </Link>
                <Link to='/sos/contest'>
                    <MenuItem
                        text='Contests'
                        iconName='numbered-list'
                    />
                </Link>
                <Link to='/sos/audit'>
                    <MenuItem
                        text='Define Audit'
                        iconName='eye-open'
                        disabled={ disableAuditButton }
                    />
                </Link>
            </Menu>
        );
    }
}

const mapStateToProps = (state: any) => ({
    currentAsmState: state.sos.asm.currentState,
    sos: state.sos,
});


export default connect(mapStateToProps)(SoSNavMenu);
