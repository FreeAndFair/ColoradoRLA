import counties from 'corla/data/counties';


function countyInfo(state: AppState): Option<CountyInfo> {
    const { county } = state;

    if (!county) { return null; }
    if (!county.id) { return null; }

    return counties[county.id];
}


export default countyInfo;
