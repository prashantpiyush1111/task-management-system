package com.TaskManagement.Security;

import java.util.*;

import com.TaskManagement.Enum.Permission;
import com.TaskManagement.Enum.Role;

public class RolebasedPermissionConfig {

    private static final Map<Role, Set<Permission>> ROLE_PERMISSION_MAP = new HashMap<>();

    static {

        ROLE_PERMISSION_MAP.put(Role.ADMIN, Set.of(
                Permission.ISSUE_VIEW,
                Permission.ISSUE_CREATE,
                Permission.ISSUE_EDIT,
                Permission.ISSUE_ASSIGN,
                Permission.ISSUE_DELETE,
                Permission.USER_MANAGE
        ));

        ROLE_PERMISSION_MAP.put(Role.MANAGER, Set.of(
                Permission.ISSUE_VIEW,
                Permission.ISSUE_CREATE,
                Permission.ISSUE_EDIT,
                Permission.ISSUE_ASSIGN
        ));

        ROLE_PERMISSION_MAP.put(Role.DEVELOPER, Set.of(
                Permission.ISSUE_VIEW,
                Permission.ISSUE_EDIT
        ));

        ROLE_PERMISSION_MAP.put(Role.TESTER, Set.of(
                Permission.ISSUE_VIEW
        ));
    }

    public static Map<Role, Set<Permission>> getrole_permission() {
        return ROLE_PERMISSION_MAP;
    }
}