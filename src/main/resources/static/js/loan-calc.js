/**
 * Apex Horizon Bank - Interactive Loan EMI Calculator
 */

function updateEmiCalculation() {
    const amountInput = document.getElementById('calcLoanAmount');
    const tenureInput = document.getElementById('calcTenureMonths');
    const rateInput = document.getElementById('calcInterestRate');

    const emiResult = document.getElementById('calcMonthlyEmiResult');
    const totalPayableResult = document.getElementById('calcTotalPayableResult');
    const totalInterestResult = document.getElementById('calcTotalInterestResult');

    if (!amountInput || !tenureInput || !rateInput || !emiResult) return;

    const P = parseFloat(amountInput.value) || 0;
    const n = parseInt(tenureInput.value) || 12;
    const annualRate = parseFloat(rateInput.value) || 8.5;

    if (P <= 0 || n <= 0) {
        emiResult.textContent = '$0.00';
        totalPayableResult.textContent = '$0.00';
        totalInterestResult.textContent = '$0.00';
        return;
    }

    const r = (annualRate / 100.0) / 12.0;
    let emi = 0;

    if (r === 0) {
        emi = P / n;
    } else {
        emi = (P * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    }

    const totalPayable = emi * n;
    const totalInterest = totalPayable - P;

    emiResult.textContent = '$' + emi.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    totalPayableResult.textContent = '$' + totalPayable.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    totalInterestResult.textContent = '$' + totalInterest.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

document.addEventListener('DOMContentLoaded', function () {
    const inputs = ['calcLoanAmount', 'calcTenureMonths', 'calcInterestRate'];
    inputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('input', updateEmiCalculation);
            el.addEventListener('change', updateEmiCalculation);
        }
    });

    const loanTypeSelect = document.getElementById('calcLoanType');
    if (loanTypeSelect) {
        loanTypeSelect.addEventListener('change', function () {
            const selectedOpt = this.options[this.selectedIndex];
            const rate = selectedOpt.getAttribute('data-rate');
            const rateInput = document.getElementById('calcInterestRate');
            if (rate && rateInput) {
                rateInput.value = rate;
                updateEmiCalculation();
            }
        });
    }

    updateEmiCalculation();
});
