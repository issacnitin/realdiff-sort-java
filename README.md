# RealDiff Java sort-stability demo

RealDiff runs the same tests on both sides of this pull request and reports the runtime values that changed.

## How it works

1. Check out the base and pull-request revisions.
2. Build both with the RealDiff Java agent attached.
3. Run the same Maven tests on both, recording observed method arguments and return values.
4. Diff those execution traces instead of inferring behavior from the source diff.

This is not mutation testing, static analysis, or coverage. No production code or test is mutated, RealDiff does not generate tests, and it observes only code this test suite executes.

## Worked example

The pull request removes a stream pipeline and sorts one copied list in place. In this block, `-` is the base and `+` is the proposal; the important change is that the new comparator uses only `priority` and drops the `code` tie-break:

```diff
-return rules.stream()
-    .sorted(Comparator.comparingInt(RuleOrdering::priority)
-        .thenComparing(RuleOrdering::code))
-    .collect(Collectors.toList());
+List<Rule> ordered = new ArrayList<>(rules);
+ordered.sort(Comparator.comparingInt(RuleOrdering::priority));
+return ordered;
```

It looks like a local allocation/performance refactor. The base resolves equal priorities alphabetically, so `A_SEASONAL` wins. Java's stable list sort preserves declaration order when the tie-break disappears, and `Z_CLEARANCE` is declared first.

The following block labels the exact values RealDiff observed before and after the edit:

```text
BASE  DiscountEngine.selectDiscount(100) -> A_SEASONAL
PR    DiscountEngine.selectDiscount(100) -> Z_CLEARANCE
BASE  CheckoutTotals.compute(100) -> 85
PR    CheckoutTotals.compute(100) -> 60
```

Neither pricing method is in the diff; only `RuleOrdering.java` changed. All three tests execute the path. The two broad total assertions still pass because 60 is discounted and does not exceed 100. Only the exact assertion that `A_SEASONAL` wins reacts.

## Why the finding is focused

RealDiff runs the base more than once and subtracts observations that disagree with themselves, removing timestamps, GUIDs, hash-order variation, and similar self-noise.

The different rule travels through a long decision pipeline, making many callers look different. RealDiff collapses those ancestors and reports the first changed behavior in unedited `DiscountEngine.java`.

## Run it

The command below runs the demo's three tests:

```bash
mvn test
```
