import { exampleState } from '../example';


export default function fetchInitialState(dispatch: any) {
    dispatch({ type: 'FETCH_INITIAL_STATE_SEND' });

    const receive = () => dispatch({
        data: exampleState,
        type: 'FETCH_INITIAL_STATE_RECEIVE',
    });

    // Simulate `fetch`.
    setTimeout(receive, 100);
}
