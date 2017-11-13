export function isCountyAppState(state: AppState): state is County.AppState {
    return state.type === 'County';
}

export function isDOSAppState(state: AppState): state is DOS.AppState {
    return state.type === 'DOS';
}
