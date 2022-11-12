package vitaliiev.resortASU.utils;


import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionsUtils {

    public static <T, R> Set<R> findNonMatching(Set<R> setToCheck, Set<R> setOfSearchedValues) {
        if (setToCheck == null || setToCheck.isEmpty()) {
            return setToCheck;
        }
        return setToCheck.stream().filter(e -> !setOfSearchedValues.contains(e)).collect(Collectors.toSet());
    }

    //    public static <T> Predicate<T> distinctByKey(Function<T, ?> fieldExtractor) {
//        Set<Object> seen = ConcurrentHashMap.newKeySet();
//        return t -> seen.add(fieldExtractor.apply(t));
//    }
//
    public static <T, R> void updateNestedEntitySetWithNewValues(R oldEntity, R newEntity,
                                                              Function<R, Set<T>> setGetter,
                                                              BiConsumer<T, R> fieldSetter,
                                                              Consumer<T> repositoryUpdater) {
        Set<T> oldNestedEntities = setGetter.apply(oldEntity);
        Set<T> newNestedEntities = setGetter.apply(newEntity);

        Set<T> elementsToUnset = findNonMatching(oldNestedEntities, newNestedEntities);
        updateNestedFieldsWithNewValue(elementsToUnset, null, fieldSetter, repositoryUpdater);
        Set<T> elementsToUpdate = findNonMatching(newNestedEntities, oldNestedEntities);
        updateNestedFieldsWithNewValue(elementsToUpdate, oldEntity, fieldSetter, repositoryUpdater);
    }

    public static <T, R> void updateNestedFieldsWithNewValue(Set<T> entities, R newFieldValue,
                                                             BiConsumer<T, R> fieldSetter,
                                                             Consumer<T> repositoryUpdater) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                fieldSetter.accept(entity, newFieldValue);
                repositoryUpdater.accept(entity);
            });
        }
    }
}
