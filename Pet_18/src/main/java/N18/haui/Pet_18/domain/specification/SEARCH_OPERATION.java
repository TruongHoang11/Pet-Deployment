package N18.haui.Pet_18.domain.specification;

import java.util.Set;

public enum SEARCH_OPERATION {
    EQUALITY, NEGATION,  GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS,
    GREATER_THAN_EQUAL, LESS_THAN_EQUAL;

    public static final Set<String> SIMPLE_OPERATION_SET =  Set.of(":", "!", ">", "<", "~", ">=", "<=");

    public static final String OR_PREDICATE_FLAG = "'";

    public static final String ZERO_OR_MORE_REGEX = "*";

    public static final String LEFT_PARENTHESIS = "(";

    public static final String RIGHT_PARENTHESIS = ")";

    public static SEARCH_OPERATION getSimpleOperation(String simpleOperation){
        return switch (simpleOperation) {
            case ":" -> EQUALITY;
            case "!" -> NEGATION;
            case ">" -> GREATER_THAN;
            case "<" -> LESS_THAN;
            case ">=" -> GREATER_THAN_EQUAL;
            case "<=" -> LESS_THAN_EQUAL;
            case "~" -> LIKE;
            default -> null;
        };
    }



}
