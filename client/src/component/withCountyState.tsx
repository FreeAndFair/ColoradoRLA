import withState from './withState';


export default function<P>(C: React.ComponentType<P>) {
    return withState('County', C);
}
