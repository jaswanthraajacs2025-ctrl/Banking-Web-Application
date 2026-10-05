/**
 * Apex Horizon Bank - Core Interactions & Helpers
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Initialize Bootstrap Tooltips & Popovers
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // 2. Copy to Clipboard utility
    document.querySelectorAll('.copy-btn').forEach(button => {
        button.addEventListener('click', function () {
            const targetText = this.getAttribute('data-copy');
            if (targetText) {
                navigator.clipboard.writeText(targetText).then(() => {
                    const originalHtml = this.innerHTML;
                    this.innerHTML = '<i class="bi bi-check-lg text-success"></i> Copied!';
                    setTimeout(() => {
                        this.innerHTML = originalHtml;
                    }, 2000);
                });
            }
        });
    });

    // 3. Quick Beneficiary Select on Transfer Form
    const beneficiarySelect = document.getElementById('beneficiarySelect');
    const recipientAccInput = document.getElementById('recipientAccountNumber');
    if (beneficiarySelect && recipientAccInput) {
        beneficiarySelect.addEventListener('change', function () {
            const selectedOption = this.options[this.selectedIndex];
            const accNo = selectedOption.getAttribute('data-acc');
            if (accNo) {
                recipientAccInput.value = accNo;
            }
        });
    }

    // 4. Dynamic Live Transfer Fee & Tax Calculator
    const transferAmountInput = document.getElementById('transferAmountInput');
    const feeDisplay = document.getElementById('feeDisplay');
    const taxDisplay = document.getElementById('taxDisplay');
    const totalDebitDisplay = document.getElementById('totalDebitDisplay');

    if (transferAmountInput && feeDisplay && taxDisplay && totalDebitDisplay) {
        const updateFeeBreakdown = () => {
            const amount = parseFloat(transferAmountInput.value) || 0;
            if (amount > 0) {
                fetch(`/api/calculate-fee?amount=${amount}`)
                    .then(res => res.json())
                    .then(data => {
                        feeDisplay.textContent = '$' + (data.serviceFee || 0).toFixed(2);
                        taxDisplay.textContent = '$' + (data.tax || 0).toFixed(2);
                        totalDebitDisplay.textContent = '$' + (data.totalDebit || 0).toFixed(2);
                    })
                    .catch(() => {
                        const fee = amount * 0.0025;
                        const tax = amount * 0.0010;
                        feeDisplay.textContent = '$' + fee.toFixed(2);
                        taxDisplay.textContent = '$' + tax.toFixed(2);
                        totalDebitDisplay.textContent = '$' + (amount + fee + tax).toFixed(2);
                    });
            } else {
                feeDisplay.textContent = '$0.00';
                taxDisplay.textContent = '$0.00';
                totalDebitDisplay.textContent = '$0.00';
            }
        };

        transferAmountInput.addEventListener('input', updateFeeBreakdown);
        transferAmountInput.addEventListener('change', updateFeeBreakdown);
        updateFeeBreakdown();
    }

    // 5. Quick Demo Account Fillers on Login Page
    const fillAdminBtn = document.getElementById('fillAdminCredentials');
    const fillCustomerBtn = document.getElementById('fillCustomerCredentials');
    const emailField = document.getElementById('email');
    const passwordField = document.getElementById('password');

    if (fillAdminBtn && emailField && passwordField) {
        fillAdminBtn.addEventListener('click', () => {
            emailField.value = 'admin@bankdemo.com';
            passwordField.value = 'Admin@123';
        });
    }

    if (fillCustomerBtn && emailField && passwordField) {
        fillCustomerBtn.addEventListener('click', () => {
            emailField.value = 'customer@bankdemo.com';
            passwordField.value = 'Customer@123';
        });
    }
});
