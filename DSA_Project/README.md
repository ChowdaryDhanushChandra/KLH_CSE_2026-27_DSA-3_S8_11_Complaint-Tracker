# TextHack – Advanced Algorithm Question Bank

**Educational Desktop Workbench for Data Structures & Algorithms – 3**

---

## Team Information
- **B Surya Prakash Reddy**
- **CH Meghanad Sai Reddy**
- **C Dhanush Chandra**

**Course:** Data Structures and Algorithms – 3 (DSA-3)  
**Platform:** Native JavaFX Desktop Application (Offline)

---

## Problem Statement
Modern computer science curricula often present advanced algorithms (e.g., dynamic programming, exact and multi-pattern string matching, suffix structures, flow networks, and randomized number theory) as disparate abstract concepts. Students frequently struggle to understand:
1. Which specific algorithmic paradigm maps to realistic text-analytics and computational linguistics queries.
2. The exact operational mechanics (comparisons, rolling hash states, failure link transitions, DP recurrence tables).
3. The empirical performance differences compared to theoretical asymptotic bounds.
4. How standard abstract data structures function when built from foundational principles rather than prepackaged library abstractions.

**TextHack** solves these challenges by providing an interactive, offline educational workbench that pairs realistic text analytics queries with manual, library-free algorithm engines and visual execution traces.

---

## Objectives
- **Zero Library Abstraction in Engine:** Implement all dynamic arrays, stacks, queues, hash maps, heaps, tries, disjoint sets, graphs, and algorithms manually without relying on `java.util.*` collections or utilities.
- **Multilingual Offline Corpus Exploration:** Support local Indian-language text analysis (English, Hindi Devanagari, and Telugu) completely offline with zero web/browser dependencies.
- **Unified Query Routing:** Enable students to submit 6 diverse query types and observe how they map to string algorithms, dynamic programming, suffix structures, max-flow networks, DAG scheduling, and randomized primality testing.
- **Interactive Algorithm Canon:** Connect each topic directly to canonical textbooks: CLRS (Part VII), Kleinberg-Tardos (Chapters 6–13), and Jeff Erickson (Chapters 4–12).
- **Side-by-Side Benchmarking:** Allow simultaneous execution of competing algorithms on identical inputs with nanosecond-level timers and comparison counters.

---

## Architecture & Design Principles

TextHack follows a strict **Layered MVC Architecture**:
```
┌─────────────────────────────────────────────────────────────────┐
│                    JavaFX Desktop UI Layer                     │
│  MainController, Module Controllers, TableViews, CSS Themes    │
└────────────────────────────────┬────────────────────────────────┘
                                 │ Model & DTOs
┌────────────────────────────────▼────────────────────────────────┐
│                         Service Layer                           │
│     QueryRouter, CorpusService, BenchmarkService               │
└────────────────────────────────┬────────────────────────────────┘
                                 │ Pure Language Primitives
┌────────────────────────────────▼────────────────────────────────┐
│           Algorithmic Engine Layer (ZERO java.util.*)           │
│   Module 1 (System), Module 2 (Strings), Module 3 (Adv. DP)     │
└────────────────────────────────┬────────────────────────────────┘
                                 │ Manual Pointers & Dynamic Arrays
┌────────────────────────────────▼────────────────────────────────┐
│                   Manual Data Structures Layer                  │
│ ManualArrayList, ManualStack, ManualQueue, ManualDeque,        │
│ ManualHashMap, ManualMinHeap, ManualPriorityQueue, ManualTrie, │
│ ManualDisjointSet, DirectedGraph, MaxFlowNetwork               │
└─────────────────────────────────────────────────────────────────┘
```

### Strict Restriction on `java.util.*`
Inside the algorithm engine, data structures, and graph packages:
- **No `java.util.*` collections** (`ArrayList`, `HashMap`, `LinkedList`, `PriorityQueue`, `HashSet`, etc.) are imported or utilized.
- **No built-in search or sorting** (`Arrays.sort()`, `Collections.sort()`, `String.indexOf()`, `contains()`, `split()`, regex) are used.
- All dynamic resizing (growth factor 2x), circular buffers, separate-chaining hash tables, min-heaps, union-find with path compression, Mergesort, and Quicksort are manually implemented.
- JavaFX collections (`ObservableList`) are quarantined strictly to UI controllers for binding data to `TableView` and `ComboBox`.

---

## Detailed Module Descriptions

### Module 1: TextHack as a System
A working text-analytics engine supporting 6 distinct query types:
1. **Pattern Search:** Dispatches queries across Naive, KMP, Z-Algorithm, and Rabin-Karp matchers.
2. **Fuzzy Matching:** Evaluates typographic and evolutionary edit distance via Wagner-Fischer, Damerau-Levenshtein (with transposition), and customizable Weighted Edit Distance.
3. **Document Similarity:** Computes Longest Common Substrings (LCS) using Generalized Suffix Arrays and Kasai LCP, paired with word-level Jaccard coefficient using `ManualHashMap`.
4. **Citation Flow Analysis:** Models academic paper citation networks as directed flow networks; computes maximum citation influence using the **Edmonds-Karp** algorithm ($O(V \cdot E^2)$).
5. **Project Scheduling:** Analyzes research project task DAGs using the **Critical Path Method (CPM)** to calculate Earliest/Latest start and slack, coupled with Graham's List Scheduling 2-approximation heuristic for parallel machines.
6. **Large-Prime Primality Testing:** Implements fast modular exponentiation $O(\log \exp)$, Fermat primality test, and Miller-Rabin randomized primality testing with error bound $\le 4^{-k}$, clearly labeling probabilistic results.

**Paradigm Mapping Dashboard:**
- Pattern Search $\rightarrow$ String Algorithms
- Fuzzy Matching $\rightarrow$ Dynamic Programming
- Document Similarity $\rightarrow$ Suffix Structures
- Citation Flow $\rightarrow$ Maximum Flow Networks
- Project Scheduling $\rightarrow$ NP-Hard & Approximation Algorithms
- Primality Testing $\rightarrow$ Randomized Algorithms

---

### Module 2: String Algorithms
Dedicated classes, interactive screens, complexity badges, and step-by-step traces for:
1. **Naive Pattern Matcher:** Exact sliding window character comparisons ($O(nm)$ worst-case).
2. **Knuth-Morris-Pratt (KMP):** Longest Proper Prefix that is also a Suffix (LPS) failure function visualization and linear-time matching ($O(n + m)$).
3. **Z-Algorithm:** Computes the Z-array over $P + '\$' + T$ using sliding window $[L, R]$ bounding prefix matches ($O(n + m)$).
4. **Rabin-Karp:** Polynomial rolling hash with prime modulus $10^9 + 7$, $O(1)$ window transitions, and spurious collision verification.
5. **Aho-Corasick:** Dictionary multi-pattern matching constructing a Trie with BFS failure links and dictionary output links for single-pass streaming ($O(n + m + z)$).
6. **Suffix Arrays:** Prefix-doubling $O(n \log^2 n)$ construction with manual mergesort, sorted suffix tables, and conceptual analysis of the linear $O(n)$ SA-IS algorithm.
7. **Kasai LCP Array:** Linear-time $O(n)$ Longest Common Prefix array construction using the theorem $LCP[rank[i]] \ge LCP[rank[i-1]] - 1$.
8. **Suffix Automata & Suffix Trees Preview:** Theoretical comparison between Directed Acyclic Word Graphs (DAWG), Ukkonen's suffix trees, and suffix arrays.

---

### Module 3: Advanced Dynamic Programming
12 dedicated interactive panels covering foundational, interval, bitmask, and tree dynamic programming:
1. **DP Framework:** Theoretical breakdown of optimal substructure, overlapping subproblems, memoization vs. tabulation, and the 4-step DP recipe.
2. **Levenshtein Distance:** Wagner-Fischer 2D DP matrix with backtracking edit scripts (`MATCH`, `INSERT`, `DELETE`, `REPLACE`).
3. **Damerau-Levenshtein Distance:** Extends Wagner-Fischer with adjacent character transpositions ($dp[i][j] = \min(\dots, dp[i-2][j-2] + 1)$).
4. **Weighted Edit Distance:** User-configurable insertion, deletion, and replacement penalties.
5. **Needleman-Wunsch:** Global pairwise sequence alignment with match rewards, mismatch penalties, gap costs, and alignment reconstruction.
6. **Smith-Waterman:** Local sequence alignment with score lower-bounded at zero ($\max(0, \dots)$), tracing back from peak score cells to highlight conserved local motifs.
7. **Matrix-Chain Multiplication:** Interval DP determining minimum scalar multiplications and optimal parenthesization tree (e.g., $((A_1 \times A_2) \times A_3)$).
8. **Optimal Binary Search Tree:** Interval DP minimizing expected search cost given search frequencies and dummy keys, with optimal root table.
9. **Bitmask Dynamic Programming:**
   - **Travelling Salesman Problem (TSP):** Held-Karp $O(2^n \cdot n^2)$ bitmask DP with optimal tour reconstruction.
   - **Hamiltonian Path Detection:** Verifies existence of simple paths visiting all vertices.
   - **Set Cover DP:** Minimum subset cover over a universe of $n$ elements.
   - *Safety Guard:* Limits and validates $n \le 16$ to prevent memory exhaustion.
10. **DP on Trees:**
    - **Maximum Weight Independent Set:** Subtree DP via $dp[u][0]$ (exclude) and $dp[u][1]$ (include).
    - **Tree Diameter DP:** Linear $O(V)$ single-pass DFS tracking top-two child branch depths.
    - **Tree Centroid DP:** Subtree size condition ($\forall v, size(v) \le n/2$).
    - **Tree Rerooting Technique:** 2-pass linear DFS computing all-pairs distance sums for every vertex in $O(V)$ time.
11. **Sum Over Subsets (SOS DP):** Bitmask DP computing $F[mask] = \sum_{sub \subseteq mask} A[sub]$ in $O(n \cdot 2^n)$ time, outperforming naive $O(3^n)$ submask iteration.
12. **When DP is the Wrong Tool:** Educational guide and decision matrix comparing DP against Greedy paradigms, lack of optimal substructure, and exponential state explosions.

---

### Supporting Systems
- **Algorithm Comparison Hub:** Benchmarks multiple algorithms simultaneously on user-supplied inputs, displaying runtimes (µs/ns), operation counts, and outputs in a JavaFX `TableView`.
- **Corpus Explorer:** Offline search across local English, Hindi, and Telugu documents.
- **Algorithm Canon:** Concordance matrix referencing CLRS (Part VII), Kleinberg-Tardos (Ch 6–13), and Erickson (Ch 4–12).
- **Algorithm Design Studio:** Interactive scenario quiz providing immediate automated evaluation of algorithm selection, rationale, and complexity analysis.

---

## Algorithm Complexity Reference Table

| Algorithm | Best Time | Average Time | Worst Time | Space | Paradigm / Data Structure |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Naive String Matching** | $O(n)$ | $O(n + m)$ | $O(n \cdot m)$ | $O(1)$ | Brute-force sliding window |
| **Knuth-Morris-Pratt (KMP)** | $O(n + m)$ | $O(n + m)$ | $O(n + m)$ | $O(m)$ | Finite state prefix function (LPS) |
| **Z-Algorithm** | $O(n + m)$ | $O(n + m)$ | $O(n + m)$ | $O(n + m)$ | Sliding match window $[L, R]$ |
| **Rabin-Karp** | $O(n + m)$ | $O(n + m)$ | $O(n \cdot m)$ | $O(1)$ | Polynomial rolling hash |
| **Aho-Corasick** | $O(n + m + z)$| $O(n + m + z)$| $O(n + m + z)$| $O(m \cdot \Sigma)$ | Trie with BFS failure & output links |
| **Suffix Array (Prefix Doubling)**| $O(n \log^2 n)$| $O(n \log^2 n)$| $O(n \log^2 n)$| $O(n)$ | Prefix rank doubling + MergeSort |
| **Kasai LCP Array** | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ | Linear rank-inversion scan |
| **Levenshtein Distance** | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | Wagner-Fischer 2D recurrence |
| **Damerau-Levenshtein** | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | 2D DP with transpositions |
| **Weighted Edit Distance** | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | Parameterized cost recurrence |
| **Needleman-Wunsch** | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | Global sequence alignment |
| **Smith-Waterman** | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | Local alignment clamped at 0 |
| **Matrix-Chain Multiplication** | $O(n^3)$ | $O(n^3)$ | $O(n^3)$ | $O(n^2)$ | Interval dynamic programming |
| **Optimal Binary Search Tree** | $O(n^3)$ | $O(n^3)$ | $O(n^3)$ | $O(n^2)$ | Interval DP over search keys |
| **Travelling Salesman (TSP DP)** | $O(2^n \cdot n^2)$ | $O(2^n \cdot n^2)$ | $O(2^n \cdot n^2)$ | $O(2^n \cdot n)$ | Held-Karp Bitmask DP ($n \le 16$) |
| **Hamiltonian Path DP** | $O(2^n \cdot n^2)$ | $O(2^n \cdot n^2)$ | $O(2^n \cdot n^2)$ | $O(2^n \cdot n)$ | Bitmask path existence |
| **Set Cover DP** | $O(2^n \cdot m)$ | $O(2^n \cdot m)$ | $O(2^n \cdot m)$ | $O(2^n)$ | Bitmask state space transition |
| **Tree Diameter DP** | $O(V)$ | $O(V)$ | $O(V)$ | $O(V)$ | Single-pass post-order DFS |
| **Tree Centroid DP** | $O(V)$ | $O(V)$ | $O(V)$ | $O(V)$ | Subtree size partition DP |
| **Tree Rerooting DP** | $O(V)$ | $O(V)$ | $O(V)$ | $O(V)$ | Two-pass top-down rerooting |
| **Sum Over Subsets (SOS DP)** | $O(n \cdot 2^n)$ | $O(n \cdot 2^n)$ | $O(n \cdot 2^n)$ | $O(2^n)$ | Dimension-by-dimension propagation |
| **Edmonds-Karp (Max-Flow)** | $O(V \cdot E^2)$ | $O(V \cdot E^2)$ | $O(V \cdot E^2)$ | $O(V + E)$ | BFS shortest augmenting paths |
| **Critical Path Method (CPM)** | $O(V + E)$ | $O(V + E)$ | $O(V + E)$ | $O(V + E)$ | DAG topological forward/backward |
| **Miller-Rabin Primality** | $O(k \log^3 n)$| $O(k \log^3 n)$| $O(k \log^3 n)$| $O(\log n)$ | Randomized Monte Carlo ($\le 4^{-k}$) |
| **Fermat Primality** | $O(k \log^3 n)$| $O(k \log^3 n)$| $O(k \log^3 n)$| $O(\log n)$ | Randomized Fermat witness test |

---

## Project Structure

```
DSA_Project/
├── pom.xml
├── README.md
├── module-info.java
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java
    │   │   └── com/
    │   │       └── texthack/
    │   │           ├── Main.java
    │   │           ├── App.java
    │   │           ├── config/
    │   │           │   └── AppConfig.java
    │   │           ├── model/
    │   │           │   ├── Document.java
    │   │           │   ├── SearchResult.java
    │   │           │   ├── AlgorithmResult.java
    │   │           │   ├── ComplexityInfo.java
    │   │           │   ├── AlignmentResult.java
    │   │           │   ├── GraphNode.java
    │   │           │   ├── GraphEdge.java
    │   │           │   ├── ScheduleTask.java
    │   │           │   └── QueryRequest.java
    │   │           ├── datastructures/
    │   │           │   ├── ManualArrayList.java
    │   │           │   ├── ManualStack.java
    │   │           │   ├── ManualQueue.java
    │   │           │   ├── ManualDeque.java
    │   │           │   ├── ManualHashMap.java
    │   │           │   ├── ManualMinHeap.java
    │   │           │   ├── ManualPriorityQueue.java
    │   │           │   ├── ManualTrie.java
    │   │           │   ├── ManualDisjointSet.java
    │   │           │   └── ManualComparator.java
    │   │           ├── graph/
    │   │           │   ├── DirectedGraph.java
    │   │           │   ├── MaxFlowNetwork.java
    │   │           │   ├── EdmondsKarp.java
    │   │           │   └── CriticalPathCalculator.java
    │   │           ├── metrics/
    │   │           │   ├── ExecutionTimer.java
    │   │           │   ├── OperationCounter.java
    │   │           │   └── ComplexityAnalyzer.java
    │   │           ├── validation/
    │   │           │   └── InputValidator.java
    │   │           ├── util/
    │   │           │   ├── ManualStringUtil.java
    │   │           │   └── ManualSort.java
    │   │           ├── engine/
    │   │           │   ├── common/
    │   │           │   │   └── StringAlgorithm.java
    │   │           │   ├── module1/
    │   │           │   │   ├── QueryRouter.java
    │   │           │   │   ├── PatternSearchService.java
    │   │           │   │   ├── FuzzyMatchingService.java
    │   │           │   │   ├── DocumentSimilarityService.java
    │   │           │   │   ├── CitationFlowService.java
    │   │           │   │   ├── ProjectSchedulingService.java
    │   │           │   │   └── PrimalityTestingService.java
    │   │           │   ├── module2/
    │   │           │   │   ├── NaivePatternMatcher.java
    │   │           │   │   ├── KMPMatcher.java
    │   │           │   │   ├── ZAlgorithmMatcher.java
    │   │           │   │   ├── RabinKarpMatcher.java
    │   │           │   │   ├── AhoCorasickMatcher.java
    │   │           │   │   ├── SuffixArray.java
    │   │           │   │   ├── SuffixArrayBuilder.java
    │   │           │   │   ├── KasaiLCP.java
    │   │           │   │   ├── SuffixAutomaton.java
    │   │           │   │   └── SuffixTreePreview.java
    │   │           │   └── module3/
    │   │           │       ├── LevenshteinDistance.java
    │   │           │       ├── DamerauLevenshteinDistance.java
    │   │           │       ├── WeightedEditDistance.java
    │   │           │       ├── NeedlemanWunsch.java
    │   │           │       ├── SmithWaterman.java
    │   │           │       ├── MatrixChainMultiplication.java
    │   │           │       ├── OptimalBinarySearchTree.java
    │   │           │       ├── TravellingSalesmanDP.java
    │   │           │       ├── HamiltonianPathDP.java
    │   │           │       ├── SetCoverDP.java
    │   │           │       ├── TreeDP.java
    │   │           │       ├── TreeDiameterDP.java
    │   │           │       ├── TreeCentroidDP.java
    │   │           │       ├── RerootingDP.java
    │   │           │       ├── SOSDP.java
    │   │           │       ├── DPFramework.java
    │   │           │       └── WhenDPIsWrong.java
    │   │           ├── controller/
    │   │           │   ├── MainController.java
    │   │           │   ├── NavigationController.java
    │   │           │   ├── Module1Controller.java
    │   │           │   ├── Module2Controller.java
    │   │           │   ├── Module3Controller.java
    │   │           │   ├── ComparisonController.java
    │   │           │   ├── CorpusController.java
    │   │           │   ├── CanonController.java
    │   │           │   └── DesignQuizController.java
    │   │           └── service/
    │   │               ├── CorpusService.java
    │   │               └── BenchmarkService.java
    │   └── resources/
    │       ├── corpus/
    │       │   ├── sample_english.txt
    │       │   ├── sample_hindi.txt
    │       │   └── sample_telugu.txt
    │       └── styles/
    │           └── application.css
    └── test/
        └── java/
            └── com/
                └── texthack/
                    ├── datastructures/
                    │   └── ManualDataStructuresTest.java
                    ├── graph/
                    │   └── GraphAlgorithmsTest.java
                    ├── module1/
                    │   └── Module1ServicesTest.java
                    ├── module2/
                    │   ├── StringMatchingTest.java
                    │   ├── SuffixStructuresTest.java
                    │   └── AhoCorasickTest.java
                    └── module3/
                        ├── EditDistanceTest.java
                        ├── SequenceAlignmentTest.java
                        ├── IntervalDPTest.java
                        ├── BitmaskDPTest.java
                        └── TreeDPTest.java
```

---

## Hardware & Software Requirements

### Minimum Hardware Requirements
- **CPU:** Dual-core 2.0 GHz or higher (x64 / ARM64)
- **RAM:** 4 GB minimum (8 GB recommended for simultaneous DP simulations)
- **Disk Space:** 300 MB free storage for JDK, JavaFX libraries, and corpus files
- **Display:** Minimum resolution of $1280 \times 800$

### Software Requirements
- **Operating System:** Windows 10/11, macOS 11+, or Linux (Ubuntu 20.04+)
- **Java Development Kit (JDK):** Java 17, 21, or 25 LTS
- **Build Tool:** Apache Maven 3.8+ (or bundled wrapper `./mvnw`)
- **IDE Compatibility:** IntelliJ IDEA, Eclipse IDE, or VS Code (with Extension Pack for Java)

---

## How to Build, Test, and Run

### 1. Compile the Project
Using the provided Maven wrapper script:
```powershell
# Windows
.\mvnw.cmd clean compile

# Linux / macOS
./mvnw clean compile
```

Or with installed Maven:
```bash
mvn clean compile
```

### 2. Run the Unit Test Suite
Execute the full unit test suite covering all data structures, graph algorithms, and DP modules:
```powershell
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### 3. Launch the JavaFX Desktop Application
Launch the native desktop window directly through the JavaFX Maven plugin:
```powershell
# Windows
.\mvnw.cmd javafx:run

# Linux / macOS
./mvnw javafx:run
```

### 4. Running in Visual Studio Code (VS Code)
1. **Open the project in VS Code:** `File -> Open Folder...` -> select `DSA_Project`.
2. **Method A (Integrated Terminal - Recommended):**
   - Press ``Ctrl + ` `` (backtick) to open the terminal.
   - Run: `.\mvnw.cmd javafx:run`
3. **Method B (VS Code Build Shortcut):**
   - Press `Ctrl + Shift + B` (runs the pre-configured `Run TextHack (javafx:run)` task in `.vscode/tasks.json`).
4. **Method C (F5 / Java Debugger):**
   - With the Microsoft *Extension Pack for Java* installed, press `F5` or click **Run** above `main()` in [`App.java`](file:///c:/Users/Dhanush/OneDrive/Desktop/DSA_Project/src/main/java/com/texthack/App.java).

Or package as a JAR and run via standard `java`:
```powershell
.\mvnw.cmd package
java -jar target/TextHack-1.0.0.jar
```

---

## Future Scope
1. **Burrows-Wheeler Transform & FM-Index:** Extend Module 2 to include linear-space compressed suffix indexing.
2. **Hopcroft-Karp Bipartite Matching:** Integrate maximum bipartite graph matching alongside Edmonds-Karp.
3. **Hirschberg's Linear-Space Alignment:** Incorporate divide-and-conquer linear space DP for gigabyte-scale sequence alignments.
4. **Interactive Graph Canvas:** Add draggable node-and-edge visualizers for flow networks and dependency DAGs.

---

## Conclusion
TextHack serves as a self-contained, rigorous educational environment for the DSA-3 curriculum. By intentionally eschewing standard library collection utilities (`java.util.*`), it provides students and educators with unfiltered transparency into fundamental memory management, algorithmic invariants, and theoretical complexities across stringology, dynamic programming, and network flows.
