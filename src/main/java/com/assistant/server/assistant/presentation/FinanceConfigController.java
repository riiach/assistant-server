package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.domain.entity.*;
import com.assistant.server.assistant.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/finance-config")
public class FinanceConfigController {
    private final AccountRepository accountRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final FinanceSummaryRepository financeSummaryRepository;

    @PostMapping("/accounts")
    public Long createAccount(@RequestBody Account request) { return accountRepository.save(request).getId(); }
    @GetMapping("/accounts")
    public List<Account> getAccounts() { return accountRepository.findAll(); }
    @PutMapping("/accounts/{id}")
    public Long updateAccount(@PathVariable Long id, @RequestBody Account request) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.update(request.getBankName(), request.getAccountName(), request.getInitialBalance(), request.getCurrentBalance());
        return accountRepository.save(account).getId();
    }
    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable Long id) { accountRepository.deleteById(id); }

    @PostMapping("/budget-categories")
    public Long createBudget(@RequestBody BudgetCategory request) { return budgetCategoryRepository.save(request).getId(); }
    @GetMapping("/budget-categories")
    public List<BudgetCategory> getBudgets() { return budgetCategoryRepository.findAll(); }
    @PutMapping("/budget-categories/{id}")
    public Long updateBudget(@PathVariable Long id, @RequestBody BudgetCategory request) {
        BudgetCategory budget = budgetCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget category not found"));
        budget.update(request.getCategory(), request.getMonthlyLimit(), request.getAlertThresholdPercent());
        return budgetCategoryRepository.save(budget).getId();
    }
    @DeleteMapping("/budget-categories/{id}")
    public void deleteBudget(@PathVariable Long id) { budgetCategoryRepository.deleteById(id); }

    @PostMapping("/summaries")
    public Long createSummary(@RequestBody FinanceSummary request) { return financeSummaryRepository.save(request).getId(); }
    @GetMapping("/summaries")
    public List<FinanceSummary> getSummaries() { return financeSummaryRepository.findAll(); }
    @PutMapping("/summaries/{id}")
    public Long updateSummary(@PathVariable Long id, @RequestBody FinanceSummary request) {
        FinanceSummary summary = financeSummaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Finance summary not found"));
        summary.update(request.getTargetYear(), request.getTargetMonth(), request.getMonthlyBudget(), request.getMonthlyIncomeTarget(), request.getMonthlySavingTarget());
        return financeSummaryRepository.save(summary).getId();
    }
    @DeleteMapping("/summaries/{id}")
    public void deleteSummary(@PathVariable Long id) { financeSummaryRepository.deleteById(id); }
}
