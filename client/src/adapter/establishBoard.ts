const formatBoardMember = (elector: any) => ({
    first_name: elector.firstName,
    last_name: elector.lastName,
    political_party: elector.politicalParty,
});


export const format = (board: any) => board.map(formatBoardMember);
