import { parse } from 'corla/adapter/uploadBallotManifest';


export default function uploadBallotManifestOk(
    state: County.AppState,
    action: Action.UploadBallotManifestOk,
): County.AppState {
    return { ...state, ...parse(action.data) };
}
