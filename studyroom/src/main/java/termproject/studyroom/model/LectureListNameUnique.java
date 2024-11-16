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
import termproject.studyroom.service.LectureListService;


/**
 * Validate that the name value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = LectureListNameUnique.LectureListNameUniqueValidator.class
)
public @interface LectureListNameUnique {

    String message() default "{Exists.lectureList.name}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class LectureListNameUniqueValidator implements ConstraintValidator<LectureListNameUnique, String> {

        private final LectureListService lectureListService;
        private final HttpServletRequest request;

        public LectureListNameUniqueValidator(final LectureListService lectureListService,
                final HttpServletRequest request) {
            this.lectureListService = lectureListService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("lectureId");
            if (currentId != null && value.equalsIgnoreCase(lectureListService.get(Integer.parseInt(currentId)).getName())) {
                // value hasn't changed
                return true;
            }
            return !lectureListService.nameExists(value);
        }

    }

}
