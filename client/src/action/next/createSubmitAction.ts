import action from '..';


interface CreateSubmitConfig {
    failType: string;
    networkFailType: string;
    okType: string;
    sendType: string;
    url: string;
}


function createSubmitAction(config: CreateSubmitConfig) {
    const {
        failType,
        networkFailType,
        okType,
        sendType,
        url,
    } = config;

    async function submitAction(body: any) {
        action(sendType);

        const init: any = {
            body: JSON.stringify(body),
            credentials: 'include',
            method: 'post',
        };

        try {
            const r = await fetch(url, init);

            if (!r.ok) {
                action(failType);
                return;
            }

            const data = await r.json();

            action(okType, data);
        } catch {
            action(networkFailType);
        }
    }

    return submitAction;
}


export default createSubmitAction;
