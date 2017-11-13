export const format = (riskLimit: number): JSON.RiskLimit =>
    ({ risk_limit: riskLimit });

export const parse = ({ risk_limit }: JSON.RiskLimit) =>
    ({ riskLimit: risk_limit });
