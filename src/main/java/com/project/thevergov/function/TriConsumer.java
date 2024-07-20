package com.project.thevergov.function;

/**
 * TriConsumer: A functional interface that represents an operation that accepts three input arguments and returns no result.
 * <p>
 * This interface is similar to {@link java.util.function.Consumer}, but it accepts three arguments instead of one.
 * It is useful for operations that require three inputs to perform an action without producing a result.
 * <p>
 * Example usage might include performing actions like logging, modifying objects, or processing data in a way that
 * requires three input parameters.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <V> the type of the third input argument
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {

    /**
     * Performs an operation on the given three input arguments.
     * <p>
     * This method is meant to be implemented to perform a specific action with the provided arguments. It does not
     * return a result and can throw exceptions if needed.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     */
    void accept(T t, U u, V v);

    // Uncomment if needed to use default method
    /*
    /**
     * Returns a composed {@code TriConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     * <p>
     * If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     *
     * @param after the operation to be performed after this operation
     * @return a composed {@code TriConsumer} that performs in sequence this operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    // default TriConsumer<T, U, V> andThen(TriConsumer<T, U, V> after) {
    //    Objects.requireNonNull(after);
    //    return (T t, U u, V v) -> {
    //        accept(t, u, v);
    //        after.accept(t, u, v);
    //    };
    // }

}
