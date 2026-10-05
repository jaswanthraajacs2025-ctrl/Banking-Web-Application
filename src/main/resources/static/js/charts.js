/**
 * Apex Horizon Bank - Charts & Financial Visualization Engine
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Customer Cash Flow Doughnut Chart (Income vs Expense)
    const cashFlowCanvas = document.getElementById('cashFlowChart');
    if (cashFlowCanvas) {
        const income = parseFloat(cashFlowCanvas.getAttribute('data-income')) || 0;
        const expenses = parseFloat(cashFlowCanvas.getAttribute('data-expenses')) || 0;

        new Chart(cashFlowCanvas, {
            type: 'doughnut',
            data: {
                labels: ['Total Income', 'Total Expenses'],
                datasets: [{
                    data: [income > 0 ? income : 100, expenses > 0 ? expenses : 0],
                    backgroundColor: ['#10b981', '#f43f5e'],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            font: { family: 'Plus Jakarta Sans', size: 12 }
                        }
                    }
                },
                cutout: '72%'
            }
        });
    }

    // 2. Customer Monthly Inflow / Outflow Bar Chart
    const monthlyActivityCanvas = document.getElementById('monthlyActivityChart');
    if (monthlyActivityCanvas) {
        new Chart(monthlyActivityCanvas, {
            type: 'bar',
            data: {
                labels: ['Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'],
                datasets: [
                    {
                        label: 'Inflow ($)',
                        data: [4200, 5800, 7100, 6400, 8500, 10000],
                        backgroundColor: '#06b6d4',
                        borderRadius: 6
                    },
                    {
                        label: 'Outflow ($)',
                        data: [2100, 3400, 4200, 3900, 5100, 6000],
                        backgroundColor: '#6366f1',
                        borderRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                        labels: { font: { family: 'Plus Jakarta Sans', size: 12 } }
                    }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: { grid: { color: '#f1f5f9' } }
                }
            }
        });
    }

    // 3. Admin Analytics: Transaction Volumes Trend
    const adminAnalyticsCanvas = document.getElementById('adminAnalyticsChart');
    if (adminAnalyticsCanvas) {
        new Chart(adminAnalyticsCanvas, {
            type: 'line',
            data: {
                labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                datasets: [
                    {
                        label: 'Deposits ($)',
                        data: [25000, 42000, 68000, 95000],
                        borderColor: '#10b981',
                        backgroundColor: 'rgba(16, 185, 129, 0.1)',
                        fill: true,
                        tension: 0.4
                    },
                    {
                        label: 'Transfers ($)',
                        data: [15000, 28000, 39000, 54000],
                        borderColor: '#6366f1',
                        backgroundColor: 'rgba(99, 102, 241, 0.1)',
                        fill: true,
                        tension: 0.4
                    },
                    {
                        label: 'Withdrawals ($)',
                        data: [8000, 14000, 21000, 31000],
                        borderColor: '#f43f5e',
                        backgroundColor: 'rgba(244, 63, 94, 0.1)',
                        fill: true,
                        tension: 0.4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top' }
                },
                scales: {
                    y: { grid: { color: '#f1f5f9' } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
});
