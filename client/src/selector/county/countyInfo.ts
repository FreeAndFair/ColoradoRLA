import counties from 'corla/data/counties';


function countyInfo(state: County.AppState): Option<CountyInfo> {
    if (!state.id) { return null; }

    return counties[state.id];
}


export default countyInfo;
