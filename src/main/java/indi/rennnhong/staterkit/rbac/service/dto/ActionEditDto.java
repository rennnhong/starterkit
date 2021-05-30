package indi.rennnhong.staterkit.rbac.service.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ActionEditDto {

    @NotEmpty
    String name;

    String icon;

    Integer sorted;

    Integer disabled;

    String functionName;
}
