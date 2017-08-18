const formatBoardMember = (elector: any) => {
    const [firstName, lastName] = elector.name.split(' ');

    return {
        first_name: firstName,
        last_name: lastName,
        political_party: elector.party,
    };
};


export const format = (board: any) => board.map(formatBoardMember);
