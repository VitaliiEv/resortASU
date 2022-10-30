package vitaliiev.resortASU.utils;


import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionElementFieldMatcher {

    /**
     * Checks if collection contains all objects from patterns
     * which specified field values matches.
     * Function ::equals is used for comparing collection
     * element field value against pattern element field value.
     */
    public static <T, R> boolean matchAllEqual(Collection<T> collection,
                                               Function<T, R> fieldExtractor,
                                               Collection<T> patterns) {
        return collection.stream()
                .distinct()
                .filter(equalsFilter(fieldExtractor,patterns))
                .count() == patterns.size();

    }

    /**
     * Checks if collection contains any objects from patterns
     * which specified field values matches.
     * Function ::equals is used for comparing collection
     * element field value against pattern element field value.
     */
    public static <T, R> boolean matchAnyEqual(Collection<T> collection,
                                               Function<T, R> fieldExtractor,
                                               Collection<T> patterns) {
        return collection.stream()
                .distinct()
                .anyMatch(equalsFilter(fieldExtractor,patterns));

    }

    /**
     * Checks if collection contains all objects from patterns
     * which specified field String value.
     * Collection object field value must contain pattern object value,
     */
    public static <T> boolean matchAllContainingStrings(Collection<T> collection,
                                                        Function<T, ? extends String> fieldExtractor,
                                                        Collection<T> patterns) {
        return collection.stream()
                .distinct()
                .filter(containsSubstringFilter(fieldExtractor,patterns))
                .count() == patterns.size();
    }

    private static <T, R> Predicate<T> equalsFilter(Function<T, R> fieldExtractor,
                                                            Collection<T> patterns) {
        return element -> patterns.stream()
                .map(fieldExtractor)
                .anyMatch(fieldValue -> fieldValue.equals(fieldExtractor.apply(element)));
    }

    private static <T> Predicate<T> containsSubstringFilter(Function<T, ? extends String> fieldExtractor,
                                                            Collection<T> patterns) {
        return element -> patterns.stream()
                .map(fieldExtractor)
                .anyMatch(fieldValue -> fieldValue.contains(fieldExtractor.apply(element)));
    }
}
