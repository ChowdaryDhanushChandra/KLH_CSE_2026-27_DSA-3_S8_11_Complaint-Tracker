# Complaint Tracker
### Smart Complaint Management Using Advanced Data Structures and Algorithms

Complaint Tracker is a JavaFX desktop application project designed to simplify the registration, tracking, assignment, prioritization, and resolution of customer complaints. It is developed for the Data Structures and Algorithms-3 (DSA-3) course.

> **Important:** Adjust the feature list and run commands to match your actual source code. Only claim that a feature or algorithm is implemented if it exists and has been tested in your repository.

## Team Details

| Team Member | Roll Number |
|---|---|
| C Dhanush Chandra | 2520030014 |
| B Surya Prakash Reddy | 2520030145 |
| CH Meghanadh Sai Reddy | 2520030274 |

- **Team Number:** 11
- **Section:** 8
- **Project Guide:** Dr. V. Sireesha, Associate Professor
- **Department:** Computer Science Engineering
- **Course:** Data Structures and Algorithms-3 (25CS2103E)
- **Academic Year:** 2026–27

## Problem Statement

Organizations can receive many complaints and may struggle to register, search, assign, track, and resolve them consistently. Manual processes can lead to delayed responses, misplaced records, limited visibility, and difficulty identifying recurring issues.

Complaint Tracker aims to provide a centralized workflow for managing complaint records from registration through resolution.

## Objectives

- Register complaints and provide a unique tracking ID.
- Allow customers to view their own complaints and track status.
- Allow authorized administrators to review, categorize, assign, prioritize, update, and resolve complaints.
- Support efficient searching and retrieval.
- Identify potentially similar complaints where duplicate detection is implemented.
- Process urgent complaints using priority-based scheduling.
- Support department assignment and workload analysis where implemented.
- Maintain complaint history and provide reports where implemented.
- Demonstrate practical applications of advanced data structures and algorithms.

## Main Features

### Customer / User
- Register a complaint.
- View personal complaints and their current status.
- Track complaints using their tracking ID.
- View complaint updates and history.
- Receive notifications or provide feedback if those features are implemented.

### Administrator
- View and manage complaints across the system.
- Search and filter complaint records.
- Update complaint priority and status.
- Assign complaints to departments or staff.
- Monitor urgent complaints and SLA risks if supported.
- View analytics, reports, and audit history if supported.

## Data Structures and Algorithms

The intended relationship between the DSA modules and complaint management is shown below.

| Algorithm / Data Structure | Intended Application |
|---|---|
| Hashing / Custom HashMap | Fast complaint lookup by ID |
| KMP string matching | Find patterns in complaint text |
| Rabin–Karp | Search text using rolling hashes |
| Edit Distance / Dynamic Programming | Estimate similarity between complaints |
| Priority Queue | Process urgent complaints first |
| Network Flow / Edmonds–Karp | Explore allocation subject to department capacity |
| Bipartite Matching | Match complaints with suitable departments or officers, where supported |

**Implementation note:** Keep this table aligned with the algorithms actually present in your repository. Mark unfinished modules as planned rather than implemented.

## Technology Stack

- **Programming Language:** Java (Java 17 or the version required by the project)
- **Desktop UI:** JavaFX
- **UI files:** FXML and JavaFX CSS, if used
- **Build tool:** Maven, if configured
- **Storage:** Specify the actual persistence method used by your application, such as SQLite, files, or in-memory storage.

## Project Structure

Your actual structure may differ. A typical JavaFX/Maven project could look like this:

```text
ComplaintTracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── complainttracker/
│   │   │       ├── models/
│   │   │       ├── controllers/
│   │   │       ├── services/
│   │   │       ├── algorithms/
│   │   │       ├── data/
│   │   │       └── Main.java
│   │   └── resources/
│   │       ├── fxml/
│   │       ├── css/
│   │       └── images/
│   └── test/
├── pom.xml
└── README.md
```

Replace the example package and folder names with the actual names in your project.

## Prerequisites

1. Install a JDK compatible with your project.
2. Install an IDE such as Visual Studio Code, IntelliJ IDEA, or Eclipse.
3. If the project uses Maven, install Maven or use your IDE's Maven support.
4. Configure JavaFX through Maven dependencies or the JavaFX SDK, as required by your project.
5. Install/configure the database only if your application uses one.

Check Java installation:

```bash
java -version
javac -version
```

If these commands are not recognized on Windows, install a JDK and configure `JAVA_HOME` and the system `Path`.

## How to Run

### Option A: Maven project

Open a terminal in the folder containing `pom.xml` and run:

```bash
mvn clean
mvn test
mvn javafx:run
```

These commands require a Maven configuration that includes the JavaFX dependencies and JavaFX Maven plugin. If your project uses another entry point or run configuration, follow the configuration in your repository.

### Option B: Run using an IDE

1. Open the project folder in your IDE.
2. Wait for the project dependencies to load.
3. Select the required JDK version.
4. Locate the JavaFX application entry point, such as `Main.java`.
5. Run it using the project's configured run profile.
6. If JavaFX modules are not configured, add the required JavaFX SDK/module path or Maven dependencies.

## Typical Complaint Workflow

1. A customer enters a complaint.
2. The application validates the information and assigns or creates a tracking ID.
3. The complaint is categorized and prioritized according to the implemented rules.
4. Similar complaints may be identified if duplicate detection is enabled.
5. An authorized administrator reviews and assigns the complaint.
6. The complaint status is updated as work progresses.
7. Resolution details and status changes are recorded in the history.
8. Reports summarize complaint data if reporting is implemented.

## Testing

For a Maven project, run:

```bash
mvn test
```

Recommended test areas:

- Complaint creation and tracking-ID uniqueness.
- Search results and matching indices.
- Edit Distance calculations and similarity thresholds.
- Priority Queue ordering.
- Network Flow or matching constraints.
- Input validation and error handling.
- Role-based access restrictions.
- Data persistence and recovery.
- JavaFX navigation, scrolling, resizing, and table visibility.

Report only test results that you have actually run.

## Screenshots

Add screenshots of the working application to a folder such as `docs/screenshots/`, then update the filenames below to match the images in your repository.

```markdown
![Dashboard](docs/screenshots/dashboard.png)
![Department Flow](docs/screenshots/department-flow.png)
![Search and Pattern Matching](docs/screenshots/search-lab.png)
![Algorithm Tests](docs/screenshots/algorithm-tests.png)
```

## Future Enhancements

Possible future improvements include:

- More advanced semantic duplicate detection.
- Email or desktop notifications.
- Multilingual complaint entry and search.
- More reporting and trend analysis.
- Improved staff scheduling and assignment constraints.
- Additional algorithm visualizations and performance comparisons.
- A mobile or web companion application.

Update this list to reflect what remains unfinished in the current project.

## Before Publishing

- Remove passwords, API keys, private connection strings, and personal data.
- Add a `.gitignore` for generated files, IDE settings, and local database files as appropriate.
- Ensure screenshots do not expose private information.
- Test the setup instructions from a clean checkout.
- Replace all example filenames and folders with the actual project structure.
- Keep the feature list accurate.

## License

No license has been specified. Add a `LICENSE` file if you want to define how others may use, modify, and distribute this project.
