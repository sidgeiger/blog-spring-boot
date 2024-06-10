package hu.progmasters.backend.exceptionhandling;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {


    private String field;

    private String errorMessage;
}
