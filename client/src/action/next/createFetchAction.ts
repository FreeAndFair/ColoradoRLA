import action from '..';


const createFetchAction = ({
    failType,
    networkFailType,
    okType,
    sendType,
    url,
}: any) => () => {
    action(sendType);

    const init: any = {
        credentials: 'include',
        method: 'get',
    };

    return fetch(url, init)
        .then(r => {
            if (!r.ok) {
                action(failType);
                return;
            }

            r.json().then(data => {
                action(okType, data);
            });
        })
        .catch(() => action(networkFailType));
};


export default createFetchAction;
