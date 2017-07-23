import * as React from 'react';
import { connect } from 'react-redux';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        return (
            <div>County Detail</div>
        );
    }
}

const mapStateToProps = () => { return; };

const mapDispatchToProps = (dispatch: any) => { return; };

export default connect(mapStateToProps, mapDispatchToProps)(CountyDetailContainer);
