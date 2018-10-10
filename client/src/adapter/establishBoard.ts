const formatBoardMember = (elector: AuditBoardMember): JSON.AuditBoardMember => {
    const { firstName, lastName } = elector;

    return {
        first_name: firstName,
        last_name: lastName,
        political_party: elector.party,
    };
};


export const format = (index: number, board: AuditBoard): object => {
    return {
        audit_board: board.map(formatBoardMember),
        index,
    };
};
