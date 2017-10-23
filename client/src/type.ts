export function isCountyAppState(state: AppState): state is County.AppState {
    return state.type === 'County';
}
