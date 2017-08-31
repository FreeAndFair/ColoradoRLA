import counties from '../../data/counties';


function countyInfo(state: any) {
    const { county } = state;

    if (!county.id) {
        return {};
    }

    return counties[county.id];
}


export default countyInfo;
