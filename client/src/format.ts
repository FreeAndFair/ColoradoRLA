function capitalize(s: string) {
    if (!s) { return ''; }

    const [fst, ...rest] = s.split('');

    return fst.toUpperCase() + rest.join('');
}

export function electionType(type: ElectionType): string {
    return `${capitalize(type)} Election`;
}
