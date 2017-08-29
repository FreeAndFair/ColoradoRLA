import * as React from 'react';

import CountyNav from '../Nav';


class EndOfRoundPage extends React.Component<any, any> {
    public render() {
        return (
            <div>
                <CountyNav />
                <div className='pt-card'>
                    End of round page.
                </div>
            </div>
        );
    }
}


export default EndOfRoundPage;
