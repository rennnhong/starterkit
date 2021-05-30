package indi.rennnhong.staterkit.util;

import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import indi.rennnhong.staterkit.rbac.entity.*;
import indi.rennnhong.staterkit.rbac.repository.*;
import indi.rennnhong.staterkit.rbac.service.PermissionService;
import indi.rennnhong.staterkit.rbac.service.RoleService;
import indi.rennnhong.staterkit.rbac.service.UserService;
import indi.rennnhong.staterkit.rbac.service.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class DataInitializer2 {

    @Autowired
    RoleService roleService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    ApiRepository apiRepository;

    Faker fakerCN = new Faker(new Locale("zh-TW"));

    Faker fakerEN = new Faker(new Locale("en_us"));

    @PostConstruct
    public void init() {

        List<Role> initRoles = Lists.newArrayList(
                new Role("系統管理員", "ROLE_ADMIN", null, new HashSet<>()),
                new Role("作業管理員", "ROLE_MANAGER", null, new HashSet<>()),
                new Role("一般使用者", "ROLE_USER", null, new HashSet<>())
        );

        Permission 根目錄 = new Permission("根目錄", null, null, 1, 1, null, null, null);
        Permission 目錄1 = new Permission("目錄1", null, null, 1, 1, null, 根目錄, null);
        Permission 目錄2 = new Permission("目錄2", null, null, 1, 1, null, 根目錄, null);
        Permission 目錄3 = new Permission("目錄3", null, null, 1, 1, null, 根目錄, null);
        Permission 目錄1_1 = new Permission("目錄1_1", null, "/users", 1, 1, null, 目錄1, null);
        Permission 目錄2_1 = new Permission("目錄2_1", null, "/roles", 1, 1, null, 目錄2, null);
        Permission 目錄3_1 = new Permission("目錄3_1", null, "/permissions", 1, 1, null, 目錄3, null);

        List<Permission> initPermissions = Lists.newArrayList(
                根目錄,
                目錄1,
                目錄2,
                目錄3,
                目錄1_1,
                目錄2_1,
                目錄3_1
        );

        String[] actionStr = new String[]{"查詢", "新增", "修改", "刪除"};

        Api apiUserGet = new Api("/api/users", "GET");
        Api apiUserPost = new Api("/api/users", "POST");
        Api apiUserPut = new Api("/api/users", "PUT");
        Api apiUserDelete = new Api("/api/users", "DELETE");
        Api apiRoleGet = new Api("/api/roles", "GET");
        Api apiRolePost = new Api("/api/roles", "POST");
        Api apiRolePut = new Api("/api/roles", "PUT");
        Api apiRoleDelete = new Api("/api/roles", "DELETE");
        Api apiPermissionGet = new Api("/api/permissions", "GET");
        Api apiPermissionPost = new Api("/api/permissions", "POST");
        Api apiPermissionPut = new Api("/api/permissions", "PUT");
        Api apiPermissionDelete = new Api("/api/permissions", "DELETE");
        List<Api> userApis = Lists.newArrayList(apiUserGet, apiUserPost, apiUserPut, apiUserDelete);
        List<Api> roleApis = Lists.newArrayList(apiRoleGet, apiRolePost, apiRolePut, apiRoleDelete);
        List<Api> permissionApis = Lists.newArrayList(apiPermissionGet, apiPermissionPost, apiPermissionPut, apiPermissionDelete);
        apiRepository.saveAll(userApis);
        apiRepository.saveAll(roleApis);
        apiRepository.saveAll(permissionApis);
        Map<String, List<Api>> apiMap = Maps.newHashMap();
        apiMap.put("/users", userApis);
        apiMap.put("/roles", roleApis);
        apiMap.put("/permissions", permissionApis);
        for (int i = 4; i < 7; i++) {
            Permission initPermission = initPermissions.get(i);
            List<Api> apis = apiMap.get(initPermission.getRoute());
            Set<Action> collect = IntStream.range(0, actionStr.length)
                    .mapToObj(index -> new Action(actionStr[index], null, null, null, null, apis.get(index)))
                    .collect(Collectors.toSet());

            initPermission.setActions(collect);
        }

        permissionRepository.saveAll(initPermissions);

        for (Role initRole : initRoles) {
            for (int i = 4; i < 7; i++) {
                Permission initPermission = initPermissions.get(i);
                for (Action action : initPermission.getActions()) {
                    if ("系統管理員".equals(initRole.getName())
                            && permissionService.isLastLayer(UUID.fromString(initPermission.getId().toString()))
                            && ("新增".equals(action.getName()) || "修改".equals(action.getName()) || "刪除"
                            .equals(action.getName()) || "查詢".equals(action.getName()))) {
                        initRole.getRolePermissions().add(new RolePermission(initPermission, action));
                    }
                    if ("作業管理員".equals(initRole.getName())
                            && permissionService.isLastLayer(UUID.fromString(initPermission.getId().toString()))
                            && ("新增".equals(action.getName()) || "修改".equals(action.getName()) || "查詢"
                            .equals(action.getName()))) {
                        initRole.getRolePermissions().add(new RolePermission(initPermission, action));
                    }
                    if ("一般使用者".equals(initRole.getName())
                            && permissionService.isLastLayer(UUID.fromString(initPermission.getId().toString()))
                            && "查詢".equals(action.getName())) {
                        initRole.getRolePermissions().add(new RolePermission(initPermission, action));
                    }

                }
            }
        }

        roleRepository.saveAll(initRoles);

        List<UserDto> initUsers = Lists.newArrayList();
        //產生隨機使用者，第0個為固定的資料
        for (int i = 0; i < 10; i++) {

            UserDto user = new UserDto();
            user.setUserName(i == 0 ? "RayLuo" : fakerCN.name().fullName());
            user.setAccount(i == 0 ? "ray1938" : fakerEN.name().firstName() + fakerEN.number().digits(5));
            user.setPassword("123456");
            user.setBirthday(fakerCN.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            user.setEmail(fakerEN.name().firstName() + fakerEN.number().digits(5) + "@gmail.com");
            user.setGender("男");
            user.setPhone(fakerCN.phoneNumber().phoneNumber());
            user.setCity(fakerCN.country().capital());
            user.setRoles(new ArrayList<>());
            int random = (int) (Math.random() * 3);
            user.getRoles().add(
                    i == 0 ? initRoles.get(2).getId().toString() : initRoles.get(random).getId().toString()
            );
            initUsers.add(user);
        }

        for (UserDto initUser : initUsers) {
            userService.save(initUser);
        }

    }


}
