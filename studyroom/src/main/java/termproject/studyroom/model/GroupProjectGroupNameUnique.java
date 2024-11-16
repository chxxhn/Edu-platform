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
import termproject.studyroom.service.GroupProjectService;


/**
 * Validate that the groupName value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = GroupProjectGroupNameUnique.GroupProjectGroupNameUniqueValidator.class
)
public @interface GroupProjectGroupNameUnique {

    String message() default "{Exists.groupProject.groupName}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GroupProjectGroupNameUniqueValidator implements ConstraintValidator<GroupProjectGroupNameUnique, String> {

        private final GroupProjectService groupProjectService;
        private final HttpServletRequest request;

        public GroupProjectGroupNameUniqueValidator(final GroupProjectService groupProjectService,
                final HttpServletRequest request) {
            this.groupProjectService = groupProjectService;
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
            final String currentId = pathVariables.get("gpId");
            if (currentId != null && value.equalsIgnoreCase(groupProjectService.get(Integer.parseInt(currentId)).getGroupName())) {
                // value hasn't changed
                return true;
            }
            return !groupProjectService.groupNameExists(value);
        }

    }

}
