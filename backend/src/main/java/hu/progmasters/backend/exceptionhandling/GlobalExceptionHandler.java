package hu.progmasters.backend.exceptionhandling;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        validationErrors.forEach(validationError -> {
            log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PostNotFoundByIdException.class)
    public ResponseEntity<List<ValidationError>> handlePostNotFoundByIdException(PostNotFoundByIdException exception) {
        ValidationError validationError = new ValidationError("PostId",
                "Post not found with id: " + exception.getPostId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundAccountById.class)
    public ResponseEntity<List<ValidationError>> handlePostNotFoundByIdException(NotFoundAccountById exception) {
        ValidationError validationError = new ValidationError("AccountId",
                "Account not found with id: " + exception.getAccountId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<List<ValidationError>> handleAccountAlreadyExistsException(AccountAlreadyExistsException exception) {
        ValidationError validationError = new ValidationError("UserName",
                "Account found with username: " + exception.getUserName());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotExistsException.class)
    public ResponseEntity<List<ValidationError>> handlePostNotExistsException(PostNotExistsException exception) {
        ValidationError validationError = new ValidationError("PostName",
                exception.getMESSAGE());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<List<ValidationError>> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
        ValidationError validationError = new ValidationError("Email",
                "Email found with : " + exception.getEMAIL());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalCommentId.class)
    public ResponseEntity<List<ValidationError>> handleIllegalCommentId(IllegalCommentId exception) {
        ValidationError validationError = new ValidationError("CommentId",
                "Illegal comment id : " + exception.getCommentId().toString());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleCommentNotFoundException(CommentNotFoundException exception) {
        ValidationError validationError = new ValidationError("CommentName",
                exception.getMESSAGE());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidEmailException.class)
    public ResponseEntity<List<ValidationError>> handleNotValidEmailException(NotValidEmailException exception) {
        ValidationError validationError = new ValidationError("Email",
                exception.getMESSAGE() + " : " + exception.getEMAIL() + " name");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordAlreadyExistsException.class)
    public ResponseEntity<List<ValidationError>> handlePasswordAlreadyExistsException(PasswordAlreadyExistsException exception) {
        ValidationError validationError = new ValidationError("Password",
                exception.getMESSAGE() + " : " + exception.getPASSWORD());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<List<ValidationError>> handlePasswordAlreadyExistsException(UnauthorizedException exception) {
        ValidationError validationError = new ValidationError("Username",
                exception.getErrorMessage());
        log.error("Error in validation: " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidPasswordException.class)
    public ResponseEntity<List<ValidationError>> handleNotValidPasswordException(NotValidPasswordException exception) {
        ValidationError validationError = new ValidationError("Password",
                exception.getMESSAGE() + " : " + exception.getPASSWORD());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyLiked.class)
    public ResponseEntity<List<ValidationError>> handleAlreadyLiked(AlreadyLiked exception) {
        ValidationError validationError = new ValidationError("Post",
                exception.getYouAlreadyLikedThisPost());
        log.error(exception.getYouAlreadyLikedThisPost() + " " + exception.getText());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotVerifiedUserException.class)
    public ResponseEntity<List<ValidationError>> handleNotVerifiedUserException(NotVerifiedUserException exception) {
        ValidationError validationError = new ValidationError("email",
                exception.getMESSAGE() + exception.getEmail());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(AlreadyFollowedUser.class)
    public ResponseEntity<List<ValidationError>> handleAlreadyFollowedUser(AlreadyFollowedUser exception) {
        ValidationError validationError = new ValidationError("User",
                "You already following " + exception.getUserName());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }
}
