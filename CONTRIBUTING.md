# Contributing Guidelines — SahanaMart

Thank you for contributing to **SahanaMart**! This document outlines our git workflow, coding conventions, definition of done, and step-by-step instructions from cloning to running a local instance.

---

## 🚀 1. From Git Clone to Running Local Instance

Follow these exact steps to stand up the application locally:

### Step 1: Clone Repository
```bash
git clone https://github.com/shabana/sahanamart.git
cd sahanamart
```

### Step 2: Environment Configuration
Copy the template configuration file:
```bash
cp .env.example .env
```
*(Ensure `.env` and `config.properties` remain gitignored and are never committed).*

### Step 3: Compile and Test
Verify that your Java environment compiles clean and all unit tests pass:
```bash
mvn clean test
```

### Step 4: Boot the Application
Run the embedded Tomcat server:
```bash
mvn compile exec:java
```
Access the application at `http://localhost:8080/`.

---

## 🌿 2. Git Branching & Commit Conventions

### Branch Model
- **`main`**: Production-ready branch. Always deployable at all times.
- **`feature/<feature-name>`**: All new development must occur on dedicated feature branches (e.g., `feature/ai-chatbot`, `feature/cart-checkout`).
- **Pull Requests**: Code is merged into `main` only via self-reviewed pull requests with passing CI.

### Conventional Commits
All commits must follow the conventional commit specification with a minimum of 3 commits per sprint:
- `feat:` A new user-facing or system feature (e.g., `feat: implement transactional checkout with stock deduction`)
- `fix:` A bug fix or edge case correction (e.g., `fix: prevent empty cart checkout submission`)
- `test:` Adding or updating unit/DAO test cases (e.g., `test: add ProductDAO integration tests with embedded H2`)
- `docs:` Documentation updates (e.g., `docs: add sequence diagram for place-order flow`)
- `refactor:` Code change that neither fixes a bug nor adds a feature

---

## 🎯 3. Definition of Done (DoD)

Every feature must satisfy the full **Definition of Done** before merging into `main`:
1. **Clean Compilation**: Compiles with zero errors or major warnings.
2. **PreparedStatement Enforcement**: 100% of SQL queries use `PreparedStatement` with parameterized placeholders. Zero string concatenations.
3. **Automated Testing**: Relevant DAO tests (embedded H2) and Service unit tests (Mockito) written and passing (`mvn clean test`).
4. **Input Validation**: Fail-fast validation at the top of service methods returning HTTP 400 before DAO execution.
5. **Security Checklist**: Passwords hashed with jBCrypt; sensitive files gitignored; custom error pages in `web.xml` hiding stack traces; output escaped in JSP with JSTL.
6. **Database Migrations**: Any schema alterations included as numbered migration files (`V2__...sql`).
7. **Green CI**: Continuous integration pipeline passing on GitHub Actions.
