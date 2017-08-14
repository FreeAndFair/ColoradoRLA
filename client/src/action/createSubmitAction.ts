import { Dispatch } from 'redux';

import { apiHost } from '../config';


const createSubmitAction = ({
    failType,
    networkFailType,
    okType,
    sendType,
    url,
}: any) => (body: any) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: sendType });

        fetch(url, { method: 'post', body })
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
