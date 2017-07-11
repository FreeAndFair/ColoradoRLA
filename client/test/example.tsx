import * as test from 'tape';
import * as React from 'react';
import { shallow } from 'enzyme';

import { Hello } from '../src/component/Hello';


test('Hello', t => {
    t.plan(1);

    const greeting = 'Hello';
    const onClick = () => {};
    const wrapper = shallow(
        <Hello onClick={ onClick } greeting={ greeting } />
    );

    t.ok(wrapper.contains(<h1>{ greeting }</h1>));
});
