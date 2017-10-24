import * as Redux from 'redux';

// Re-export `Dispatch` to the global namespace so we can use it in
// one of our own globals.
declare global {
    type Dispatch<S> = Redux.Dispatch<S>;
}
