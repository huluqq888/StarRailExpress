package io.wifi.starrailexpress.api;

import io.wifi.starrailexpress.SRE;
import org.ladysnake.cca.api.v3.component.ComponentKey;

public class SRERoles {
  public static final SRERole DISCOVERY_CIVILIAN = registerRole(
      new NormalRole(SRE.id("pending"), 0x5CFF4A, false, false, SRERole.MoodType.NONE, -1, true))
      .setCanPickUpRevolver(false).setCanUseKiller(true).setNeutrals(true).setNeutralForKiller(false);

  public static SRERole registerRole(SRERole role) {
    TMMRoles.ROLES.put(role.identifier(), role);
    if (role.getComponentKey() != null) {
      TMMRoles.COMPONENT_KEYS.add(role.getComponentKey());
    }
    return role;
  }

  public static void addRoleComponents(ComponentKey<? extends RoleComponent> componentKeyToAdd) {
    TMMRoles.COMPONENT_KEYS.add(componentKeyToAdd);
  }
}
