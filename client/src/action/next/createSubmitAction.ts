import action from '..';


const createSubmitAction = ({
    failType,
    networkFailType,
    okType,
    sendType,
    url,
}: any) => (body: any) => {
    action(sendType);

    const init: any = {
        body: JSON.stringify(body),
        credentials: 'include',
        method: 'post',
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


export default createSubmitAction;
