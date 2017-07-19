import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Popover } from '@blueprintjs/core';

import Nav from './Nav';
import NavMenu from './NavMenu';


test('Nav', t => {
    t.plan(4);

    const rendered = shallow(<Nav />);

    t.ok(rendered.is('nav.pt-navbar'),
         'is a styled <nav> element');

    const popover = rendered.find(Popover).first();

    t.ok(popover.exists(), 'it has a <Popover>');

    const content: any = popover.prop('content');

    t.ok(content, 'the <Popover> has content');

    t.equals(content.type, NavMenu, 'the content should be a <NavMenu>');
});
