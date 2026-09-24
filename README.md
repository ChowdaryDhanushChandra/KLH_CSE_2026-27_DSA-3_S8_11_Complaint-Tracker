<<<<<<< HEAD
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
=======
# Complaint Tracker

## 👥 Team Members

| Name | ID |
|---|---|
| **B SURYA PRAKASH REDDY** | `25020030145` |
| **CH MEGHANADH SAI REDDY** | `2520030274` |
| **C DHANUSH CHANDRA** | `2520030014` |

### Course Instructor

**DR. V. Sireesha**

---

## 📌 Project Description

Organizations receive a large number of customer complaints every day, making it difficult to manage, track, and resolve issues efficiently using manual processes. Delayed responses, misplaced records, and poor tracking can negatively affect customer satisfaction and service quality.

The **Complaint Tracker** provides a centralized system for registering, tracking, categorizing, assigning, updating, and resolving complaints. Each complaint is assigned a unique tracking ID and stored securely, allowing quick retrieval and efficient record management.

The system also maintains complaint history, supports search functionality, and generates reports for identifying recurring issues.

---

## 🎯 Objectives

- Register and manage customer complaints efficiently.
- Assign a unique tracking ID to each complaint.
- Categorize complaints based on their type.
- Assign complaints to the appropriate staff or department.
- Track the status of complaints.
- Maintain complaint history.
- Provide efficient search functionality.
- Generate reports for analyzing complaints.
- Reduce manual effort and response time.
- Improve customer satisfaction and service quality.

---

## ⚙️ Key Features

### 📝 Complaint Registration
Users can register new complaints and provide relevant details such as customer information, complaint description, category, priority, and date.

### 🔍 Complaint Tracking
Each complaint is assigned a unique **Complaint ID**, which can be used to track its current status and details.

### 📂 Complaint Categorization
Complaints can be categorized based on their type, such as:

- Technical Issues
- Billing Issues
- Product Issues
- Service Issues
- Delivery Issues
- Other Issues

### 👨‍💼 Complaint Assignment
Complaints can be assigned to the appropriate employee or department for resolution.

### 🔄 Status Management
The complaint status can be updated throughout its lifecycle:

```text
Pending → Assigned → In Progress → Resolved → Closed
>>>>>>> 0313e70cf4639d09d351dcc12491b6aadfd965dd
