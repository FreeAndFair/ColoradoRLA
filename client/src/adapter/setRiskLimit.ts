export const format = (riskLimit: number) => ({ risk_limit: riskLimit });

export const parse = ({ risk_limit }: any) => ({ riskLimit });
