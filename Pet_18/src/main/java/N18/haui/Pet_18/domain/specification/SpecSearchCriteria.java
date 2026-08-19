package N18.haui.Pet_18.domain.specification;


import lombok.Getter;

import static N18.haui.Pet_18.domain.specification.SEARCH_OPERATION.OR_PREDICATE_FLAG;


@Getter
public class SpecSearchCriteria {
    private final String key;
    private final SEARCH_OPERATION operation;
    private final Object value;
    private final boolean orPredicate;

    private SpecSearchCriteria(String key, SEARCH_OPERATION searchOperation, Object value){
        this(null, key, searchOperation, value);
    }

    public SpecSearchCriteria(String orPredicate, String key, SEARCH_OPERATION searchOperation, Object value){
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
    }

    public SpecSearchCriteria(String orPredicate, String key, String operation, Object value, String prefix, String suffix) {
        if(operation == null || operation.isEmpty()){
            throw new IllegalArgumentException("Search operation cannot be empty or null");
        }

        SEARCH_OPERATION searchOperation = SEARCH_OPERATION.getSimpleOperation(operation);

        if(searchOperation != null){
            // 🛠️ FIX: Nếu là phép toán tìm kiếm bình thường bằng dấu `:` hoặc `~`
            if(searchOperation == SEARCH_OPERATION.EQUALITY || searchOperation == SEARCH_OPERATION.LIKE) {
                boolean startWithAsterisks = (prefix != null && prefix.contains(SEARCH_OPERATION.ZERO_OR_MORE_REGEX))
                        || (value != null && value.toString().startsWith(SEARCH_OPERATION.ZERO_OR_MORE_REGEX));
                boolean endWithAsterisks = (suffix != null && suffix.contains(SEARCH_OPERATION.ZERO_OR_MORE_REGEX))
                        || (value != null && value.toString().endsWith(SEARCH_OPERATION.ZERO_OR_MORE_REGEX));

                if(startWithAsterisks && endWithAsterisks){
                    searchOperation = SEARCH_OPERATION.CONTAINS;
                } else if (startWithAsterisks) {
                    searchOperation = SEARCH_OPERATION.ENDS_WITH;
                } else if(endWithAsterisks){
                    searchOperation = SEARCH_OPERATION.STARTS_WITH;
                } else {
                    // Mặc định đối với trường Text, nếu không chỉ định cụ thể, nên chuyển sang dạng CONTAINS để dễ tìm kiếm
                    searchOperation = SEARCH_OPERATION.CONTAINS;
                }
            }
        }

        this.key = key;
        this.operation = searchOperation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
        // ko co predicate mac dinh la and
    }
    public static void handleWildCardSearch(String valueStr, String orPredicate,String prefix, String suffix, boolean isOrPredicate){
        if(valueStr.startsWith("*")){
            prefix = "*";
            valueStr = valueStr.substring(1); // 0 - 1
        }
        if(valueStr.endsWith("*")){
            suffix = "*";
            valueStr = valueStr.substring(0, valueStr.length() - 1);
        }
        isOrPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
    }
    // substring: begin index  ( index start 0)
//    "hello".substring(1)  →  "ello"
//    "hello".substring(2)  →  "llo"
    // substring: begin index to end index
    // "hello".substring(0,3) -> "hel"
    // "hello".substring(1,3) -> "el"
    // "hello".substring(0, hello.length() - 1) -> "hell"
}
