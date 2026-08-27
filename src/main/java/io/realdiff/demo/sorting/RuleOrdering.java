package io.realdiff.demo.sorting;

import io.realdiff.demo.pricing.Rule;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class RuleOrdering {
    private RuleOrdering() {
    }

    public static List<Rule> byPriority(List<Rule> rules) {
        return rules.stream()
            .sorted(Comparator.comparingInt(RuleOrdering::priority)
                .thenComparing(RuleOrdering::code))
            .collect(Collectors.toList());
    }

    private static int priority(Rule rule) {
        return rule.priority;
    }

    private static String code(Rule rule) {
        return rule.code;
    }
}