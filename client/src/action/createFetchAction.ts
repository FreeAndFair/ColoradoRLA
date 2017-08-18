import { Dispatch } from 'redux';


const createFetchAction = ({
    failType,
    networkFailType,
    okType,
    sendType,
    url,
}: any) => () => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: sendType });

        const init: any = {
            credentials: 'include',
            method: 'get',
        };

        fetch(url, init)
            .then(r => {
                if (!r.ok) {
                    dispatch({ type: failType });
                    return;
                }

                r.json().then((data: any) => {
                    dispatch({ type: okType, data });
                });
            })
            .catch(() => {
                dispatch({ type: networkFailType });
            });
    };
};


export default createFetchAction;
