import * as _ from 'lodash';


const fmtKey = (k: string): string => {
    const words = k.split('_');

    if (words.length === 1) {
        return k;
    }

    const [w, ...rest] = words;

    return [w].concat(rest.map(_.capitalize)).join('');
};

const fmtArray = (o: any[]): any => o.map(fmtAny);

const fmtObject = (o: any): any => {
    const newO: any = {};

    _.forEach(o, (v, k) => {
        newO[fmtKey(k)] = fmtAny(v);
    });

    return newO;
};

const fmtAny = (o: any): any => {
    if (_.isPlainObject(o)) {
        return fmtObject(o);
    }

    if (_.isArray(o)) {
        return fmtArray(o);
    }

    return o;
};


export default fmtAny;
