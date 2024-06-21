package com.project.thevergov.enumeration.converter;

import com.project.thevergov.enumeration.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

/**
 * RoleConverter: A JPA AttributeConverter that handles the conversion between the `Authority` enum and its String representation
 * for persistence in the database.
 */
@Converter(autoApply = true) // Automatically applies this converter to all Authority attributes
public class RoleConverter implements AttributeConverter<Authority, String> {

    /**
     * convertToDatabaseColumn: Converts an `Authority` enum value to its corresponding String representation for storage.
     *
     * @param authority The Authority enum value to be converted.
     * @return The String representation of the authority, or null if the input is null.
     */
    @Override
    public String convertToDatabaseColumn(Authority authority) {
        return (authority == null) ? null : authority.getValue();
    }

    /**
     * convertToEntityAttribute: Converts a String representation of an authority back into its corresponding `Authority` enum value.
     *
     * @param code The String representation of the authority to be converted.
     * @return The Authority enum value matching the code, or throws IllegalAccessError if no match is found.
     */
    @Override
    public Authority convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Authority.values())
                .filter(authority -> authority.getValue().equals(code))
                .findFirst() // Get the first matching Authority, if any
                .orElseThrow(IllegalAccessError::new); // Throw an error if no match found (should be IllegalArgumentException ideally)
    }
}
