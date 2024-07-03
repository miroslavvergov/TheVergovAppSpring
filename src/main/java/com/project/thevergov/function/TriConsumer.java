package com.project.thevergov.function;


@FunctionalInterface
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);


//    default Consumer<T, U, V> andThen(TriConsumer<T, U, V> after) {
//        Objects.requireNonNull(after);
//        return (T t, U u, V v) -> {
//            accept(t, u, v);
//            after.accept(t,u, v);
//        };
//    }

}
