package indi.rennnhong.staterkit.rbac.service.dto;

import lombok.Data;

@Data
public class UserPermissionDto {

    private PermissionDto permission;

    private ActionDto action;

}
