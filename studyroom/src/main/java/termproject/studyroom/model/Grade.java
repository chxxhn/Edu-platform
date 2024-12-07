package termproject.studyroom.model;

import lombok.Getter;


@Getter
public enum Grade {

    STD("std"),
    PROF("prof"),
    TA("ta"),
    LEAD("lead");


    private String value;

    Grade(String value) {
        this.value = value;
    }
}
