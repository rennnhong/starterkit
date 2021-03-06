package indi.rennnhong.staterkit.module.student.web.command;

import javax.validation.constraints.*;

/**
 * bean of create action
 */
public class StudentCreateCommand {
    @NotEmpty
    private String name;

    @NotNull
    @Min(value = 0)
    @Max(value = 100)
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}


