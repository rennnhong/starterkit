package indi.rennnhong.staterkit.rbac.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleEditDto {

    String name;

    String code;

    List<RolePermissionDto> rolePermissions;
}
