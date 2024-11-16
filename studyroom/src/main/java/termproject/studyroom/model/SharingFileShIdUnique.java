package termproject.studyroom.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;
import termproject.studyroom.service.SharingFileService;


/**
 * Validate that the sharingId value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = SharingFileShIdUnique.SharingFileShIdUniqueValidator.class
)
public @interface SharingFileShIdUnique {

    String message() default "{Exists.sharingFile.shId}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SharingFileShIdUniqueValidator implements ConstraintValidator<SharingFileShIdUnique, Integer> {

        private final SharingFileService sharingFileService;
        private final HttpServletRequest request;

        public SharingFileShIdUniqueValidator(final SharingFileService sharingFileService,
                final HttpServletRequest request) {
            this.sharingFileService = sharingFileService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Integer value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("shfId");
            if (currentId != null && value.equals(sharingFileService.get(Integer.parseInt(currentId)).getShId())) {
                // value hasn't changed
                return true;
            }
            return !sharingFileService.shIdExists(value);
        }

    }

}
