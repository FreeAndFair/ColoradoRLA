import * as React from 'react';

import {
    BrowserRouter as Router,
    Route,
} from 'react-router-dom';

import { Hello } from '../component/Hello';


export class App extends React.Component {
    render() {
        return (
            <Router>
                <div>
                    <Route exact path='/' component={Hello} />
                    <Route path='/hola' component={Hello} />
                    <Route path='/hello' component={Hello} />
                </div>
            </Router>
        );
    }
}
