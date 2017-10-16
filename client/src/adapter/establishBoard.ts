const formatBoardMember = (elector: AuditBoardMember): AuditBoardMemberJson => {
    const { firstName, lastName } = elector;

    return {
        first_name: firstName,
        last_name: lastName,
        political_party: elector.party,
    };
};


export const format = (board: AuditBoard): AuditBoardJson =>
    board.map(formatBoardMember);
