export const format = (riskLimit: number): RiskLimitJson =>
    ({ risk_limit: riskLimit });

export const parse = ({ risk_limit }: RiskLimitJson) =>
    ({ riskLimit: risk_limit });
