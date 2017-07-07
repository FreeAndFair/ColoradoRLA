import * as test from 'tape';
import * as React from 'react';
import { shallow } from 'enzyme';

import { Hello } from '../src/component/Hello';


test('Hello', t => {
    t.plan(1);

    const wrapper = shallow(<Hello greeting='Hello' />);

    t.ok(wrapper.contains(<h1>Hello</h1>));
});
