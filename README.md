# RealDiff Java sort-stability demo

A three-test fixture for adding a deterministic code tie-break to a stable priority sort. The pull request edits only `RuleOrdering.java`; the behavior frontier remains in unedited pricing code, and only the exact tie-winner assertion reacts.

Run locally with `mvn test`.
