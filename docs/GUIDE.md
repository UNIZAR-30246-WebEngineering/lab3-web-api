---
title: "Web Engineering 2026-2027"
subtitle: "Lab 3: Web API"
date: "2026-10-19"
format:
  html:
    toc: true
    toc-depth: 3
    number-sections: true
    code-fold: true
    code-tools: true
    code-overflow: wrap
    theme: cosmo
    css: |
      body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
      .quarto-title-block { border-bottom: 2px solid #2c3e50; padding-bottom: 1rem; }
      h1, h2, h3 { color: #2c3e50; }
      pre { background-color: #f8f9fa; border-left: 4px solid #007acc; }
      .callout { border-left: 4px solid #28a745; }
      pre, code { white-space: pre-wrap; word-break: break-word; overflow-wrap: anywhere; }
  pdf:
    documentclass: article
    classoption: [11pt, a4paper]
    toc: true
    toc-depth: 3
    number-sections: true
    geometry: [margin=2.5cm, headheight=15pt]
    fontsize: 11pt
    linestretch: 1.15
    colorlinks: true
    breakurl: true
    urlcolor: blue
    linkcolor: blue
    citecolor: blue
    hyperrefoptions:
      - linktoc=all
      - bookmarksnumbered=true
      - bookmarksopen=true
    header-includes:
      - |
        \usepackage{helvet}
        \renewcommand{\familydefault}{\sfdefault}
        \usepackage{hyperref}
        \usepackage{fancyhdr}
        \pagestyle{fancy}
        \fancyhf{}
        \fancyhead[L]{Web Engineering 2026-2027}
        \fancyhead[R]{Lab 3: Web API}
        \fancyfoot[C]{\thepage}
        \renewcommand{\headrulewidth}{0.4pt}
        \usepackage{microtype}
        \usepackage{booktabs}
        \usepackage{array}
        \usepackage{longtable}
        \usepackage{xcolor}
        \definecolor{sectioncolor}{RGB}{44,62,80}
        \usepackage{sectsty}
        \allsectionsfont{\color{sectioncolor}}
        \usepackage{fvextra}
        \fvset{breaklines=true, breakanywhere=true}
        \DefineVerbatimEnvironment{Highlighting}{Verbatim}{breaklines,breakanywhere,commandchars=\\\{\}}
lang: en-GB
---

Third lab of 2026--2027. Command line is enough; **VS Code**, **IntelliJ IDEA**, **Eclipse**, or **GitHub Codespaces** are fine. You need **Java 25 LTS** (same pin as the group project).

## Requirements

- [Kotlin 2.4.0](https://kotlinlang.org/) on **Java 25 LTS**. Do not downgrade the toolchain.
- [Gradle 9.6.0](https://gradle.org/) via the wrapper (`./gradlew`).
- [Spring Boot 4.1.0](https://docs.spring.io/spring-boot/) (Spring Framework 7) with `spring-boot-starter-webmvc`.
- Spring Data JPA and H2. The employee resource is already mapped.
- [MockK](https://mockk.io/) through SpringMockK, for the repository doubles in `ControllerTests`.
- [Ktlint](https://pinterest.github.io/ktlint/) on the build.

## Git and clone

1. Install [git](https://git-scm.com/).
2. GitHub education account: real name, university email.
3. Configure Git:

   ```bash
   git config --global user.name "Your Real Name"
   git config --global user.email "your_nip@unizar.es"
   ```

4. Authenticate with GitHub so you can clone this starter if Git asks. HTTPS is enough: [`gh auth login`](https://docs.github.com/en/github-cli/github-cli/quickstart) or Git Credential Manager. Do not use an account password. [Set up Git](https://docs.github.com/en/get-started/git-basics/set-up-git).

```bash
git clone \
  https://github.com/UNIZAR-30246-WebEngineering/lab3-web-api.git
cd lab3-web-api
./gradlew check
./gradlew bootRun
```

`./gradlew check` fails on the starter. That failure is the assignment: the five tests stop on `TODO` until you fill the blocks below. After the tests pass, the API listens on <http://localhost:8080> (`GET /employees`).

### Codespaces (optional)

You can do the lab in [GitHub Codespaces](https://docs.github.com/en/codespaces) instead of a local JDK. Forward port **8080** when you `./gradlew bootRun`.

You cannot push to the course repository. If you need a remote of your own (Codespaces persistence), **fork** first and open the Codespace from that fork. A fork is not a submission: delivery is the Moodle zip only.

## Objective

Finish `ControllerTests` so the suite shows which HTTP methods are safe and which are idempotent. Document that in **REPORT.md**. Kotlin: [KDoc](https://kotlinlang.org/docs/kotlin-doc.html) if you add types of your own.

The controller is already implemented:

- `POST /employees` — create. Not safe, not idempotent.
- `GET /employees/{id}` — read one employee, or `404`. Safe and idempotent. The missing employee is requested twice.
- `GET /employees` — list. Safe and idempotent.
- `PUT /employees/{id}` — replace, or create at that id. Not safe, idempotent.
- `DELETE /employees/{id}` — delete. Not safe, idempotent. A repeated `DELETE`, including one for an id that is already absent, stays `204` (RFC 9110 §9.3.5, §15.3.5).

The requests, status codes, and JSON expectations are already written. The comment above each setup separates the initial state from the runtime state. Your setup answers both. The repository is a mock, so each answer is the state at that moment. You replace each `TODO` with a setup or a `verify` from the blocks below. Do not change the controller to make a test pass.

### Oral defence (all students)

An oral defence is **possible for everyone**, not only for the bonus. The teacher may ask you to explain this lab in person.

You are expected to **know** the submission and to **justify all decisions** in it: what you specified, what you changed, what you rejected, how you verified it, and any AI use. If you cannot explain a choice, you do not own it.

### Safe and idempotent

Properties below are from [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110) (HTTP Semantics). It obsoletes RFC 7231.

- **Safe** ([§9.2.1](https://www.rfc-editor.org/rfc/rfc9110#section-9.2.1)): the method does not request a change of origin-server state. `GET` is safe ([§9.3.1](https://www.rfc-editor.org/rfc/rfc9110#section-9.3.1)). A safe test must show that `save` and `deleteById` are not called. `findAll` is also a read. The item `GET` must not call it because that handler does not list the collection.
- **Idempotent** ([§9.2.2](https://www.rfc-editor.org/rfc/rfc9110#section-9.2.2)): the intended effect on the server of repeating the request is the same as one request. The response status does not have to be identical. `GET`, `PUT`, and `DELETE` are idempotent. `POST` is not ([§9.3.3](https://www.rfc-editor.org/rfc/rfc9110#section-9.3.3)): two identical creates yield two employees and two ids.

### Imports

Add these to `ControllerTests` when you paste a block. `any()` is available inside `every` and `verify`; do not import it.

```kotlin
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import java.util.Optional
```

`@AutoConfigureMockMvc` is already imported from `org.springframework.boot.webmvc.test.autoconfigure`.

### Blocks

Use these blocks to complete each test.

**Setup: save returns a new id on each call**

```kotlin
every {
    employeeRepository.save(any<Employee>())
} answers {
    Employee("Mary", "Manager", 1)
} andThenAnswer {
    Employee("Mary", "Manager", 2)
}
```

**Setup: findById returns an employee for id 1**

```kotlin
every {
    employeeRepository.findById(1)
} answers {
    Optional.of(Employee("Mary", "Manager", 1))
}
```

**Setup: findById returns empty for id 2**

```kotlin
every {
    employeeRepository.findById(2)
} answers {
    Optional.empty()
}
```

**Setup: findById is empty, then returns the employee**

```kotlin
every {
    employeeRepository.findById(1)
} answers {
    Optional.empty()
} andThenAnswer {
    Optional.of(Employee("Tom", "Manager", 1))
}
```

**Setup: save returns the employee at id 1**

```kotlin
every {
    employeeRepository.save(any<Employee>())
} answers {
    Employee("Tom", "Manager", 1)
}
```

**Setup: deleteById may be called**

*`justRun` stubs a method that returns nothing.*

```kotlin
justRun {
    employeeRepository.deleteById(1)
}
```

**Setup: findAll returns one employee**

```kotlin
every {
    employeeRepository.findAll()
} answers {
    listOf(Employee("Mary", "Manager", 1))
}
```

**Verify: both saves discard the client id**

```kotlin
verify(exactly = 2) {
    employeeRepository.save(Employee("Mary", "Manager", null))
}
```

**Verify: no read or delete**

```kotlin
verify(exactly = 0) {
    employeeRepository.findById(any())
    employeeRepository.findAll()
    employeeRepository.deleteById(any())
}
```

**Verify: findById(1) was called twice**

```kotlin
verify(exactly = 2) {
    employeeRepository.findById(1)
}
```

**Verify: findById(2) was called twice**

```kotlin
verify(exactly = 2) {
    employeeRepository.findById(2)
}
```

**Verify: no modifying methods were called**

```kotlin
verify(exactly = 0) {
    employeeRepository.save(any())
    employeeRepository.deleteById(any())
    employeeRepository.findAll()
}
```

**Verify: both saves store the same employee**

```kotlin
verify(exactly = 2) {
    employeeRepository.save(Employee("Tom", "Manager", 1))
}
```

**Verify: no delete or list**

```kotlin
verify(exactly = 0) {
    employeeRepository.deleteById(any())
    employeeRepository.findAll()
}
```

**Verify: findAll was called twice**

```kotlin
verify(exactly = 2) {
    employeeRepository.findAll()
}
```

**Verify: the list does not write or read one id**

```kotlin
verify(exactly = 0) {
    employeeRepository.save(any())
    employeeRepository.deleteById(any())
    employeeRepository.findById(any())
}
```

**Verify: deleteById was called twice, and nothing else**

```kotlin
verify(exactly = 2) {
    employeeRepository.deleteById(1)
}
verify(exactly = 0) {
    employeeRepository.save(any())
    employeeRepository.findById(any())
    employeeRepository.findAll()
}
```

### Manual verification

1. If a method is safe, the test must show that repository writes are not called.
2. If a method is idempotent, a second identical request must not allocate a new id or leave a different state.
3. Run the tests:

```bash
./gradlew test
```

### Code quality

[Ktlint](https://pinterest.github.io/ktlint/) is on the build. Format before you submit:

```bash
./gradlew ktlintFormat
./gradlew ktlintCheck
```

### What to document

English, at least B1. Cover what **you** added: which block you chose for each method, and how the verify counts show safety or idempotency. Commands in the report must actually work.

### Bonus

The bonus is optional. The bonus is **+0.05** on factor **k** (labs; cap **+0.10** across the six labs).

It is not extra work you add at submit time. Sequence:

1. The **student proposes** the extra (what you will do, why it is outstanding, how git history will stay readable). Propose during the lab window, **before** you treat that extra as bonus work.
2. The **instructor accepts** — or refuses if the proposal is too large, off-scope, or late.
3. **Only then** may that extra be done as bonus work.

Examples you may propose: OpenAPI for this resource; RFC 9457 problem details for `EmployeeNotFoundException`; Bean Validation on `Employee`; a test that uses H2 instead of a mock; pagination on `GET /employees`; cache headers on the safe method.

After acceptance implement what was agreed, with tests and documentation another student could follow, then defend it in person.

Git is **mandatory** for the bonus: imperative commit subjects, small logical commits, feature branches, no dump-everything commits. `git rebase -i` is allowed to tidy *your* unpublished history; do not rewrite commits already in a zip you submitted.

Those commits must be in the Moodle zip’s `.git`. You do not push to the course repository.

## Finish and submit

Keep the **REPORT.md** headings. It is an engineering note (specified, changed, verified, AI), not a restatement of the starter README.

**Only files under git control are reviewed.** Commit what you want evaluated. Untracked files do not count.

The Moodle zip must include the **`.git` directory** (the whole repository, not only the working tree).

```bash
./gradlew check
./gradlew clean
zip -r lab3-web-api-submission.zip . \
  -x ".gradle/*" ".kotlin/*" "build/*" ".idea/*" "*.iml" ".DS_Store"
```

*Windows:* a `\` at the end of a line continues the command in bash. Command Prompt and PowerShell do not treat it that way, so the `zip` line breaks. Run this block in [WSL](https://learn.microsoft.com/en-us/windows/wsl/) (Windows Subsystem for Linux).

Do not zip Gradle/Kotlin caches, `build/`, or IDE files.

Deadline **30 October 2026, 23:59** (one week after the last Lab 3 session). Submit **only on Moodle**: the zip after `./gradlew clean`, **with `.git`**. There is no GitHub delivery.

### REPORT.md

Do **not** delete the headings. Do **not** report a percentage of “AI vs original lines”.

1. **What I specified** — safety and idempotency for each method *before* generating or pasting code, and how you would know the tests pass
2. **What I changed** — files and behaviour
3. **Technical decisions** — which block you used, and what you rejected
4. **How I verified** — `./gradlew check`, what failed first, what you fixed
5. **AI disclosure** — the [AI use](#ai-use) table, **or** **No AI assistance**

**Thin (fails):** `Tools: ChatGPT. Used for: the assignment.`

**Adequate:** named tool; one concrete purpose; a prompt or two; files touched; `./gradlew check` plus what you changed after the model’s draft.

## AI use

The group project scores **AI use (10%)**: GenAI and agents to boost delivery, with disclosure you can defend. This lab is where you **practise that disclosure**. Lab 3 itself stays **limited**: assistive GenAI only — **not** a full or substantial generated solution. You may add an `AGENTS.md` or a project skill so the agent follows this guide ([Agent instructions](#agent-instructions)).

If you cannot explain it without the tool, you do not own it. This course grades **web engineering judgment** (specify, design, verify).

### Agent instructions

You may give the agent this lab as **one** of these files.

**Always on: `AGENTS.md`.** Put it in the repository root. Cursor and Claude Code read it on every turn in this repo. Plain Markdown, with no YAML header:

```markdown
# Lab 3

The tasks and status codes are in `docs/GUIDE.md`.
Work only on the step I name.
Edit `src/test/kotlin/es/unizar/webeng/lab3/ControllerTests.kt` unless I name another file.
After an edit, run `./gradlew check` and report the result.
```

Claude Code also reads `CLAUDE.md` from the repository root. Same text, that filename, when Claude is the tool you use.

**On demand: a skill.** The agent loads it when your request matches the `description`. Claude looks in `.claude/skills/lab3-web-api/SKILL.md`. Cursor looks in `.cursor/skills/lab3-web-api/SKILL.md`.

```markdown
---
name: lab3-web-api
description: Use when working on the Web Engineering Lab 3 employee API in this repository.
---

# Lab 3 Web API

The tasks and status codes are in `docs/GUIDE.md`.
Work only on the step I name.
Edit `src/test/kotlin/es/unizar/webeng/lab3/ControllerTests.kt` unless I name another file.
After an edit, run `./gradlew check` and report the result.
```

The `description` is the trigger. The lines under the title are the orders, the same orders as in `AGENTS.md`.

The examples are a start. Rewrite them for the way you ask: the words in `description`, the file you are editing, and the checks you want run (`./gradlew check`, `./gradlew ktlintCheck`).

Record the file under **Tools / skills** in **REPORT.md**.

### Allowed (assistive)

- Brainstorming and outlines
- Boilerplate and scaffolding
- Debugging help; refactoring and lint fixes
- Test skeletons
- Grammar and style on **REPORT.md**
- An `AGENTS.md` or a project skill that points the agent at this guide ([Agent instructions](#agent-instructions))

### Not allowed

- A full or substantial GenAI solution for this individual lab
- Fabricated results, tests, or citations
- Prompts meant to hide assistance
- Pasting exam, personal, or third-party confidential / proprietary material into tools. Your **own** lab in a local IDE agent / Copilot is allowed.

### Minimum if you used AI

Same fields as the project **AI use** slice. Fill them in **REPORT.md**. “I used ChatGPT” is not enough.

| Field | What to write |
| --- | --- |
| **Tools / skills** | Named GenAI and agent tools |
| **Purpose** | What you asked the tool to do (a step, not “do the lab”) |
| **Representative prompts** | Short examples, or an appendix |
| **Affected files/sections** | Where assistance landed |
| **Validation steps** | What you ran (`./gradlew check`) and what you changed |
| **Citations** | External snippets you adapted |
| **Human-reviewed** | What you kept, edited, or rejected |

If you used no AI, write **No AI assistance**.

We do **not** use AI detectors. We may look at commits and **REPORT.md**.
