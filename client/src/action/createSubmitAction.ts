import { Dispatch } from 'redux';


const createSubmitAction = ({
    failType,
    networkFailType,
    okType,
    sendType,
    url,
}: any) => (body: any) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: sendType });

        const init: any = {
            body: JSON.stringify(body),
            credentials: 'include',
            method: 'post',
        };

        return fetch(url, init)
            .then(r => {
                if (!r.ok) {
                    dispatch({ type: failType });
                    return;
                }

                r.json().then((data: any) => {
                    dispatch({ type: okType, data, sent: body });
                });
            })
            .catch(() => {
                dispatch({ type: networkFailType });
            });
    };
};


export default createSubmitAction;
