import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Popover } from '@blueprintjs/core';

import withNav from './withNav';


test('withNav', t => {
    t.plan(4);

    const Menu = () => <div>Example</div>;

    const Nav = withNav(Menu, '/root');

    const c = shallow(<Nav />);

    t.ok(c.is('nav.pt-navbar'), 'is a styled <nav> element');

    const popover = c.find(Popover).first();
    t.ok(popover.exists(), 'it has a <Popover>');

    const content: any = popover.prop('content');

    t.ok(content, 'the <Popover> has content');
    t.equals(content.type, Menu, 'the content should be the passed <Menu>');
});
