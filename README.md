# Samadhan – Product & Package Complaint Center (JavaFX)
Run: `mvn javafx:run` (JDK 21+, Maven). Logins: admin / Admin@123 (Admin tab), customer / Customer@123 (Customer tab), or create a customer account on the login page.
Engine code uses no java.util collections (own DynArray + arrays); java.util.Random is used for sampling only.

| Feature (UI name)              | Folder          | Algorithm |
|--------------------------------|-----------------|-----------|
| Services, login, registration  | module1_system  | Service facade |
| Smart Search, duplicates       | module2_strings | KMP |
| Suggested category while typing| module2_strings | Aho-Corasick |
| Typo-tolerant search           | module3_dp      | Edit distance |
| Pickup Routes                  | module3_dp      | Bitmask DP (exact travelling salesman) |
| Auto-Assignment                | module4_flow    | Edmonds-Karp max-flow |
| Resolution Optimizer           | module5_np      | 0/1 knapsack DP |
| Audit & Security               | module6_random  | Reservoir sampling, Miller-Rabin |
| Most urgent complaints         | module6_random  | Randomized quicksort |
| Insights: repeated complaint phrase | module2_strings | Suffix array (prefix doubling) + Kasai LCP |
| Insights: courier scorecard, assignment bottleneck | module1 / module4 | Aggregation, max-flow leftovers |
Data is saved to data/samadhan.tsv on logout/close; "Export report" writes data/report.csv. Light/dark theme switch is in the sidebar.
| Algorithm Lab                  | module2_strings | Naive vs KMP vs Z-function vs Rabin-Karp, with step counts and timing |
| Escalation Paths               | module3_dp      | Tree DP (longest chain) |
| Assign at lowest travel cost   | module4_flow    | Min-cost max-flow (successive shortest paths) |
| Fast approximate optimizer     | module5_np      | Knapsack FPTAS, compared with the exact DP |
| Primality check (Algorithm Lab)| module6_random  | Miller-Rabin |
