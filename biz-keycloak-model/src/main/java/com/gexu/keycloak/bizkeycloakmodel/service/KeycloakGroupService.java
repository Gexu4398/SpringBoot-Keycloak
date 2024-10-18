package com.gexu.keycloak.bizkeycloakmodel.service;

import cn.hutool.core.collection.CollUtil;
import com.gexu.keycloak.bizkeycloakmodel.model.Group;
import com.gexu.keycloak.bizkeycloakmodel.model.KeycloakRole;
import com.gexu.keycloak.bizkeycloakmodel.model.Role;
import com.gexu.keycloak.bizkeycloakmodel.model.User;
import com.gexu.keycloak.bizkeycloakmodel.model.UserAttribute;
import com.gexu.keycloak.bizkeycloakmodel.model.UserEntity;
import com.gexu.keycloak.bizkeycloakmodel.repository.EventEntityRepository;
import com.gexu.keycloak.bizkeycloakmodel.repository.KeycloakGroupRepository;
import com.gexu.keycloak.bizkeycloakmodel.repository.UserEntityRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KeycloakGroupService {

  private final KeycloakService keycloakService;

  private final KeycloakGroupRepository keycloakGroupRepository;

  private final UserEntityRepository userEntityRepository;

  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public KeycloakGroupService(KeycloakService keycloakService,
      KeycloakGroupRepository keycloakGroupRepository, UserEntityRepository userEntityRepository,
      EventEntityRepository eventEntityRepository) {

    this.keycloakService = keycloakService;
    this.keycloakGroupRepository = keycloakGroupRepository;
    this.userEntityRepository = userEntityRepository;
    this.eventEntityRepository = eventEntityRepository;
  }

  public List<Group> getPosterity(String groupId) {

    if (groupId == null) {
      return keycloakGroupRepository
          .findAll()
          .stream()
          .map(it -> Group.builder()
              .id(it.getId())
              .name(it.getName())
              .build())
          .toList();
    }

    final var result = new LinkedList<Group>();
    final var groups = keycloakGroupRepository
        .findAllByParentGroup(groupId)
        .stream()
        .map(it -> Group.builder()
            .id(it.getId())
            .name(it.getName())
            .build())
        .toList();
    for (final var group : groups) {
      result.addAll(getPosterity(group.getId()));
    }
    result.addAll(groups);
    return result;
  }

  public Group newGroup(Group group) {

    final var groupResource = keycloakService.newGroupResource(group.getName(),
        group.getParentId());
    final var groupRepresentation = groupResource.toRepresentation();
    return getGroup(groupRepresentation.getId());
  }

  public Group renameGroup(String id, String newName) {

    keycloakService.renameGroupResource(id, newName);
    return getGroup(id);
  }

  public Group moveGroup(String id, String parentId) {

    keycloakService.moveGroupResource(id, parentId);
    return getGroup(id);
  }

  public Group getGroup(String id) {

    final var groupResource = keycloakService.getGroupResource(id);
    final var groupRepresentation = groupResource.toRepresentation();
    return Group.builder()
        .id(groupRepresentation.getId())
        .name(groupRepresentation.getName())
        .build();
  }

  public List<Group> getGroups() {

    return keycloakGroupRepository
        .findAll()
        .stream()
        .map(it -> Group.builder()
            .id(it.getId())
            .name(it.getName())
            .parentId(it.getParentGroup())
            .build())
        .toList();
  }

  public void deleteGroup(String id) {

    final var groupResource = keycloakService.getGroupResource(id);
    groupResource.remove();
  }

  public List<User> getGroupUsers(String id) {

    return userEntityRepository.findByGroups_IdAndUsernameNotLike(id).stream()
        .map(this::getUserResponse)
        .toList();
  }

  private User getUserResponse(UserEntity user) {

    final var builder = User.builder()
        .id(user.getId())
        .username(user.getUsername())
        .enabled(user.getEnabled())
        .phoneNumber(user
            .getAttributes()
            .stream()
            .filter(it -> "phoneNumber".equals(it.getName()))
            .findFirst().orElseGet(UserAttribute::new)
            .getValue())
        .name(user.getFirstName())
        .createdAt(user.getCreatedTimestamp());

    if (CollUtil.isNotEmpty(user.getRoles())) {
      final var roles = user.getRoles()
          .stream()
          .filter(KeycloakRole::getClientRole)
          .map(role -> Role.builder()
              .id(role.getId())
              .name(role.getName())
              .build())
          .collect(Collectors.toSet());
      builder.role(roles);
    }

    if (CollUtil.isNotEmpty(user.getGroups())) {
      final var group = user.getGroups().stream().findFirst().orElseThrow();
      builder.group(Group.builder()
          .id(group.getId())
          .name(group.getName())
          .build());
    }

    eventEntityRepository.countByIpAddress(user.getId()).stream().findFirst().ifPresent(it ->
        builder.commonIp(it[0].toString()));

    eventEntityRepository.findFirstByUserIdAndTypeIsOrderByEventTimeDesc(user.getId(), "LOGIN")
        .ifPresent(it -> {
          builder.lastLoginIp(it.getIpAddress());
          builder.lastLoginTime(it.getEventTime());
        });

    return builder.build();
  }
}
