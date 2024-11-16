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
import termproject.studyroom.service.OldExamFileService;


/**
 * Validate that the oeId value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = OldExamFileOeIdUnique.OldExamFileOeIdUniqueValidator.class
)
public @interface OldExamFileOeIdUnique {

    String message() default "{Exists.oldExamFile.oeId}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OldExamFileOeIdUniqueValidator implements ConstraintValidator<OldExamFileOeIdUnique, Integer> {

        private final OldExamFileService oldExamFileService;
        private final HttpServletRequest request;

        public OldExamFileOeIdUniqueValidator(final OldExamFileService oldExamFileService,
                final HttpServletRequest request) {
            this.oldExamFileService = oldExamFileService;
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
            final String currentId = pathVariables.get("oefId");
            if (currentId != null && value.equals(oldExamFileService.get(Integer.parseInt(currentId)).getOeId())) {
                // value hasn't changed
                return true;
            }
            return !oldExamFileService.oeIdExists(value);
        }

    }

}
