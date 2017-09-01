function currentBallotNumber(state: any): number {
    const { county } = state;
    const { ballotsToAudit, currentBallot } = county;

    if (!ballotsToAudit || !currentBallot) {
        return null;
    }

    return 1 + ballotsToAudit.indexOf(currentBallot.id);
}


export default currentBallotNumber;
