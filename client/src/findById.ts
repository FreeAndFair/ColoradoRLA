import * as _ from 'lodash';


export default function findById(arr: any[], id: any) {
    return _.find(arr, (o: any) => o.id === id);
}
