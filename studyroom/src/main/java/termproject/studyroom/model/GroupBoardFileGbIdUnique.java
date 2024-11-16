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
import termproject.studyroom.service.GroupBoardFileService;


/**
 * Validate that the gbId value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = GroupBoardFileGbIdUnique.GroupBoardFileGbIdUniqueValidator.class
)
public @interface GroupBoardFileGbIdUnique {

    String message() default "{Exists.groupBoardFile.gbId}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GroupBoardFileGbIdUniqueValidator implements ConstraintValidator<GroupBoardFileGbIdUnique, Integer> {

        private final GroupBoardFileService groupBoardFileService;
        private final HttpServletRequest request;

        public GroupBoardFileGbIdUniqueValidator(final GroupBoardFileService groupBoardFileService,
                final HttpServletRequest request) {
            this.groupBoardFileService = groupBoardFileService;
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
            final String currentId = pathVariables.get("gbfId");
            if (currentId != null && value.equals(groupBoardFileService.get(Integer.parseInt(currentId)).getGbId())) {
                // value hasn't changed
                return true;
            }
            return !groupBoardFileService.gbIdExists(value);
        }

    }

}
