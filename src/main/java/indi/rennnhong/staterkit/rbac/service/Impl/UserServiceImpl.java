package indi.rennnhong.staterkit.rbac.service.Impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import indi.rennnhong.staterkit.rbac.entity.Role;
import indi.rennnhong.staterkit.rbac.entity.User;
import indi.rennnhong.staterkit.common.exception.ExceptionFactory;
import indi.rennnhong.staterkit.common.exception.ExceptionType;
import indi.rennnhong.staterkit.rbac.repository.PermissionRepository;
import indi.rennnhong.staterkit.rbac.repository.RoleRepository;
import indi.rennnhong.staterkit.rbac.repository.UserRepository;
//import indi.rennnhong.staterkit.rbac.security.UserDetailsImpl;
import indi.rennnhong.staterkit.rbac.service.UserService;
import indi.rennnhong.staterkit.rbac.service.dto.UserDto;
import indi.rennnhong.staterkit.rbac.service.dto.UserEditDto;
import indi.rennnhong.staterkit.common.query.PageableResult;
import indi.rennnhong.staterkit.common.query.PageableResultImpl;
import indi.rennnhong.staterkit.common.query.QueryParameter;
import indi.rennnhong.staterkit.rbac.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static indi.rennnhong.staterkit.common.exception.GroupType.USER;


@Service
@Transactional
class UserServiceImpl implements UserService{

    final UserRepository userRepository;

    final UserMapper userMapper;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public UserDto getUserByAccount(String account) {
        Optional<User> optionalUser = userRepository.findByAccount(account);
        User user = optionalUser.orElseThrow(() ->
                ExceptionFactory.newExceptionWithId(USER, ExceptionType.ENTITY_NOT_FOUND, 2, account)
        );
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return Lists.newArrayList(userMapper.toDto(users));
    }

    @Override
    public UserDto getById(UUID uuid) {
        Optional<User> optionalUser = userRepository.findById(uuid);
        User user = optionalUser.orElseThrow(() ->
                ExceptionFactory.newException(USER, ExceptionType.ENTITY_NOT_FOUND, uuid.toString())
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        if (userRepository.existsByAccount(userDto.getAccount()))
            throw ExceptionFactory.newException(USER, ExceptionType.DUPLICATE_ENTITY, userDto.getAccount());

        User user = userMapper.createEntity(userDto);

        //處理Roles
        if (!ObjectUtils.isEmpty(userDto.getRoles())) setUserRoles(user, userDto.getRoles());

//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto update(UUID id, UserEditDto dto) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() ->
                ExceptionFactory.newException(USER, ExceptionType.ENTITY_NOT_FOUND, id.toString())
        );

        userMapper.updateEntity(user, dto);

        //處理Roles
        user.getRoles().clear();
        if (!ObjectUtils.isEmpty(dto.getRoleIds())) setUserRoles(user, dto.getRoleIds());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    private void setUserRoles(User user, List<String> roleIds) {
        if(user.getRoles() == null) user.setRoles(Sets.newHashSet());

        List<UUID> uuidList = roleIds.stream()
                .map(roleId -> UUID.fromString(roleId))
                .collect(Collectors.toList());
        List<Role> roles = roleRepository.findAllByIdIn(uuidList);
        user.getRoles().addAll(roles);
    }

    @Override
    public void delete(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() ->
                ExceptionFactory.newException(USER, ExceptionType.ENTITY_NOT_FOUND, id.toString())
        );
        userRepository.delete(user);
    }

    @Override
    public boolean isExist(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public PageableResult<UserDto> pageAll(Integer pageNumber, Integer rowsPerPage) {
        QueryParameter qp = new QueryParameter()
                .addPageNumber(pageNumber)
                .addRowsPerPage(rowsPerPage)
                .build();

        Page<User> resultPage = userRepository.findAll(
                PageRequest.of(qp.getPageOffset(), qp.getPageLimit()));

        List<UserDto> userDtos = ImmutableList.copyOf(userMapper.toDto(resultPage.getContent()));

        return new PageableResultImpl<UserDto>(
                qp.getPageLimit(),
                qp.getPageNumber(),
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                userDtos);
    }

    @Override
    public boolean isExistByAccount(String userAccount) {
        return userRepository.existsByAccount(userAccount);
    }

    @Override
    public Collection<UserDto> getUsersOfRole(UUID roleId) {
        Role role = roleRepository.findById(roleId).get();
        List<Role> roles = Collections.singletonList(role);
        Collection<User> users = userRepository.findAllByRolesIsIn(roles);
        return userMapper.toDto(users);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByAccount(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//        return UserDetailsImpl.build(user);
//    }
}
