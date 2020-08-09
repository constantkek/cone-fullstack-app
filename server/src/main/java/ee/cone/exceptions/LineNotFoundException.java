package ee.cone.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Nothing to change.")
public class LineNotFoundException extends RuntimeException {
}
