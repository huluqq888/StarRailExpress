package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.SRE;
import io.wifi.starrailexpress.api.CustomWinnerRole;
import io.wifi.starrailexpress.api.NormalRole;
import io.wifi.starrailexpress.api.SRERole;
import io.wifi.starrailexpress.api.TMMRoles;
import io.wifi.starrailexpress.cca.SREGameWorldComponent;
import io.wifi.starrailexpress.cca.SRETrainWorldComponent;
import io.wifi.starrailexpress.content.entity.PlayerBodyEntity;
import io.wifi.starrailexpress.content.block.ToiletBlock;
import io.wifi.starrailexpress.game.GameConstants;
import io.wifi.starrailexpress.game.GameUtils;
import io.wifi.starrailexpress.game.ShopContent;
import io.wifi.starrailexpress.index.TMMItems;
import io.wifi.starrailexpress.util.ShopEntry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.agmas.noellesroles.game.roles.Innocent.coroner.BodyDeathReasonComponent;
import org.agmas.noellesroles.utils.RoleUtils;

import java.util.List;
import java.util.ArrayList;

public class DNF {
    public static final ResourceLocation KILLER_ID = SRE.id("dnf_killer");
    public static final ResourceLocation SOLDIER_ID = SRE.id("dnf_soldier");
    public static final ResourceLocation CHEF_ID = SRE.id("dnf_chef");
    public static final ResourceLocation PSYCHOLOGIST_ID = SRE.id("dnf_psychologist");
    public static final ResourceLocation LOCKSMITH_ID = SRE.id("dnf_locksmith");
    public static final ResourceLocation CIVILIAN_ID = SRE.id("dnf_civilian");
    public static final ResourceLocation FLYING_KNIFE_DEATH = SRE.id("dnf_flying_knife");

    public static final int BLOOD_PER_CORPSE = 10;
    public static final int BLOOD_PRICE = 10;
    public static final int KNIFE_COOLDOWN_TICKS = 50 * 20;
    public static final int CROWBAR_COOLDOWN_TICKS = 300 * 20;
    public static final int TWO_DAYS_TICKS = 2 * 24000;
    public static final float SAN_TASK_MOOD_GAIN = 0.4f;
    public static final int CHEF_DAILY_FOOD_CAPACITY = 40;
    public static final int CHEF_WATER_CHECK_COST = 10;
    public static final int CHEF_RECIPE_OUTPUT = 10;
    public static final int INITIAL_CAFETERIA_FOOD = 50;

    private static boolean eventsRegistered;

    public static final SRERole KILLER = TMMRoles.registerRole(
            new CustomWinnerRole(KILLER_ID, 0x7A1414, false, true, SRERole.MoodType.FAKE, -1, true) {
                @Override
                public void onInit(net.minecraft.server.MinecraftServer server, ServerPlayer serverPlayer) {
                    DNFPlayerComponent.KEY.get(serverPlayer).init();
                    if (isNight(serverPlayer)) {
                        equipNightTools(serverPlayer);
                    }
                }

                @Override
                public List<ItemStack> getDefaultItems() {
                    ArrayList<ItemStack> items = new ArrayList<>();
                    items.add(DNFItems.BLOOD_BUY_FLYING_KNIFE.getDefaultInstance());
                    items.add(DNFItems.BLOOD_BUY_LOCKPICK.getDefaultInstance());
                    return items;
                }

                @Override
                public void serverTick(ServerPlayer player) {
                    DNFPlayerComponent component = DNFPlayerComponent.KEY.get(player);
                    if (player.level().getGameTime() % 20 == 0) {
                        updateNightTools(player);
                        component.checkHunger(player);
                    }
                }

                @Override
                public InteractionResult rightClickEntity(Player player, Entity target) {
                    if (!(player instanceof ServerPlayer serverPlayer) || !(target instanceof PlayerBodyEntity body)) {
                        return InteractionResult.PASS;
                    }
                    if (!GameUtils.isPlayerAliveAndSurvival(serverPlayer)) {
                        return InteractionResult.PASS;
                    }
                    return eatBody(serverPlayer, body);
                }

                @Override
                public void onAbilityUse(Player player) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        exchangeBlood(serverPlayer, serverPlayer.isShiftKeyDown());
                    }
                }

                @Override
                public boolean onUseKnife(Player player) {
                    if (!isNight(player)) {
                        player.displayClientMessage(Component.translatable("message.dnf.killer.night_only")
                                .withStyle(ChatFormatting.DARK_RED), true);
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean onUseKnifeHit(Player player, Player target) {
                    if (!isNight(player)) {
                        return false;
                    }
                    return true;
                }

                @Override
                public GameUtils.WinStatus checkWin(ServerPlayer player, GameUtils.WinStatus winStatus) {
                    return DNFPlayerComponent.KEY.get(player).hasPersonalEnding()
                            ? GameUtils.WinStatus.CUSTOM
                            : GameUtils.WinStatus.NOT_MODIFY;
                }

                @Override
                public void win(ServerPlayer player) {
                    var roundEnd = io.wifi.starrailexpress.cca.SREGameRoundEndComponent.KEY.get(player.serverLevel());
                    roundEnd.CustomWinnerTitle = Component.translatable("game.win.star.dnf_killer");
                    roundEnd.CustomWinnerSubtitle = Component.translatable("message.dnf.killer.ending");
                    RoleUtils.customWinnerWin(player.serverLevel(), this.identifier().getPath(), this.color());
                }

                @Override
                public boolean didPlayerWin(ServerPlayer player, boolean original, GameUtils.WinStatus winStatus) {
                    if (winStatus == GameUtils.WinStatus.CUSTOM || winStatus == GameUtils.WinStatus.CUSTOM_COMPONENT) {
                        return DNFPlayerComponent.KEY.get(player).hasPersonalEnding();
                    }
                    return original;
                }
            }.setComponentKey(DNFPlayerComponent.KEY).setCanSeeCoin(false).setCanUseInstinct(true).setMax(4)
                    .setCanSeeTeammateKiller(false));

    public static final SRERole SOLDIER = TMMRoles.registerRole(new NormalRole(SOLDIER_ID, 0x496D89, true, false,
            SRERole.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(), false).setVigilanteTeam(true).setMax(2));
    public static final SRERole CHEF = TMMRoles.registerRole(new NormalRole(CHEF_ID, 0x8C6A2D, true, false,
            SRERole.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(), false) {
        @Override
        public void onInit(net.minecraft.server.MinecraftServer server, ServerPlayer serverPlayer) {
            DNFPlayerComponent.KEY.get(serverPlayer).init();
        }

        @Override
        public void onAbilityUse(Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                DNFItems.tryChefWork(serverPlayer, serverPlayer.isShiftKeyDown());
            }
        }

        @Override
        public InteractionResult rightClickEntity(Player player, Entity target) {
            if (!(player instanceof ServerPlayer serverPlayer) || !(target instanceof PlayerBodyEntity body)) {
                return InteractionResult.PASS;
            }
            if (!GameUtils.isPlayerAliveAndSurvival(serverPlayer)) {
                return InteractionResult.PASS;
            }
            return DNFItems.cookBodyAsChef(serverPlayer, body);
        }
    }.setMax(1));
    public static final SRERole PSYCHOLOGIST = TMMRoles.registerRole(new NormalRole(PSYCHOLOGIST_ID, 0x8E6BC6, true,
            false, SRERole.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(), false).setMax(2));
    public static final SRERole LOCKSMITH = TMMRoles.registerRole(new NormalRole(LOCKSMITH_ID, 0xD1A448, true, false,
            SRERole.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(), false) {
        @Override
        public InteractionResult onUseBlock(Player player, net.minecraft.world.level.Level world,
                net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hitResult) {
            return DNFItems.tryRepairLockpickedDoor(player, world, hitResult.getBlockPos());
        }
    }.setMax(4));
    public static final SRERole CIVILIAN = TMMRoles.registerRole(new NormalRole(CIVILIAN_ID, 0x719E5B, true, false,
            SRERole.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(), false));

    public static void init() {
        DNFItems.init();
        TMMRoles.addRoleComponents(DNFPlayerComponent.KEY);
        registerEvents();
        registerBloodShop();
        DNFDebugCommand.register();
    }

    public static boolean isDNFKiller(SRERole role) {
        return role != null && role.identifier().equals(KILLER_ID);
    }

    public static boolean isDNFKiller(Player player) {
        return player != null && isDNFKiller(SREGameWorldComponent.KEY.get(player.level()).getRole(player));
    }

    public static boolean isDNFLocksmith(Player player) {
        return player != null && SREGameWorldComponent.KEY.get(player.level()).isRole(player, LOCKSMITH);
    }

    public static boolean isDNFChef(Player player) {
        return player != null && SREGameWorldComponent.KEY.get(player.level()).isRole(player, CHEF);
    }

    public static boolean isDayNightFightMode(Level world) {
        return world != null && SREGameWorldComponent.KEY.get(world).getGameMode().identifier
                .equals(io.wifi.starrailexpress.api.SREGameModes.DAY_NIGHT_FIGHT_ID);
    }

    public static boolean isNight(Player player) {
        if (player == null) {
            return false;
        }
        SRETrainWorldComponent.TimeOfDay timeOfDay = SRETrainWorldComponent.KEY.get(player.level()).getTimeOfDay();
        return player.level().isNight()
                || timeOfDay == SRETrainWorldComponent.TimeOfDay.NIGHT
                || timeOfDay == SRETrainWorldComponent.TimeOfDay.MIDNIGHT
                || timeOfDay == SRETrainWorldComponent.TimeOfDay.SUNDOWN;
    }

    public static void applyKnifeCooldown(ServerPlayer player) {
        if (isDNFKiller(player)) {
            player.getCooldowns().addCooldown(TMMItems.KNIFE, KNIFE_COOLDOWN_TICKS);
        }
    }

    private static InteractionResult eatBody(ServerPlayer player, PlayerBodyEntity body) {
        DNFPlayerComponent component = DNFPlayerComponent.KEY.get(player);
        ResourceLocation bodyRole = BodyDeathReasonComponent.KEY.get(body).playerRole;
        component.eatCorpse(player, bodyRole, BLOOD_PER_CORPSE);
        body.discard();
        player.level().playSound(null, body.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.8f, 0.55f);

        if (component.hasPersonalEnding()) {
            player.displayClientMessage(Component.translatable("message.dnf.killer.ending")
                    .withStyle(ChatFormatting.DARK_PURPLE), false);
            if (player.serverLevel().getGameTime() % 10 == 0) {
                player.displayClientMessage(Component.translatable("game.win.star.dnf_killer")
                        .withStyle(ChatFormatting.DARK_PURPLE), true);
            }
        } else {
            player.displayClientMessage(Component.translatable("message.dnf.killer.blood",
                    component.getBlood(), component.getBodiesEaten()).withStyle(ChatFormatting.RED), true);
        }
        return InteractionResult.SUCCESS;
    }

    private static void exchangeBlood(ServerPlayer player, boolean lockpick) {
        DNFPlayerComponent component = DNFPlayerComponent.KEY.get(player);
        if (!component.spendBlood(BLOOD_PRICE)) {
            player.displayClientMessage(Component.translatable("message.dnf.killer.not_enough_blood", BLOOD_PRICE)
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        ItemStack stack = lockpick ? DNFItems.LOCKPICK.getDefaultInstance() : DNFItems.FLYING_KNIFE.getDefaultInstance();
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
        player.displayClientMessage(Component.translatable(lockpick
                ? "message.dnf.killer.exchange_lockpick"
                : "message.dnf.killer.exchange_flying_knife", component.getBlood()).withStyle(ChatFormatting.RED),
                true);
    }

    private static void updateNightTools(ServerPlayer player) {
        if (isNight(player)) {
            equipNightTools(player);
        } else {
            removeNightTools(player);
        }
    }

    private static void equipNightTools(ServerPlayer player) {
        ensureHas(player, TMMItems.KNIFE.getDefaultInstance());
        ensureHas(player, DNFItems.CROWBAR.getDefaultInstance());
    }

    private static void removeNightTools(ServerPlayer player) {
        removeAll(player, TMMItems.KNIFE);
        removeAll(player, DNFItems.CROWBAR);
    }

    private static void ensureHas(ServerPlayer player, ItemStack stack) {
        if (player.getInventory().contains(stack)) {
            return;
        }
        if (!player.addItem(stack.copy())) {
            player.drop(stack.copy(), false);
        }
    }

    private static void removeAll(ServerPlayer player, net.minecraft.world.item.Item item) {
        for (List<ItemStack> compartment : player.getInventory().compartments) {
            for (int i = 0; i < compartment.size(); i++) {
                if (compartment.get(i).is(item)) {
                    compartment.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide || !(player instanceof ServerPlayer serverPlayer)
                    || !isDayNightFightMode(world) || !GameUtils.isPlayerAliveAndSurvival(serverPlayer)) {
                return InteractionResult.PASS;
            }

            BlockState state = world.getBlockState(hitResult.getBlockPos());
            DNFPlayerComponent component = DNFPlayerComponent.KEY.get(serverPlayer);
            if (state.is(Blocks.COBWEB)) {
                if (component.cleanLibraryWeb(serverPlayer)) {
                    world.destroyBlock(hitResult.getBlockPos(), false, serverPlayer);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            if (state.is(Blocks.LECTERN)) {
                return component.completeLecture(serverPlayer) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
            if (state.getBlock() instanceof ToiletBlock) {
                return component.completeToilet(serverPlayer) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
            if (isDustBlock(state)) {
                if (component.cleanPrisonDust(serverPlayer)) {
                    world.destroyBlock(hitResult.getBlockPos(), false, serverPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }

    private static boolean isDustBlock(BlockState state) {
        return state.is(Blocks.GRAY_CARPET)
                || state.is(Blocks.LIGHT_GRAY_CARPET)
                || state.is(Blocks.BLACK_CARPET)
                || state.is(Blocks.GRAY_CONCRETE_POWDER)
                || state.is(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    }

    public static void registerBloodShop() {
        ArrayList<ShopEntry> shop = new ArrayList<>();
        shop.add(new BloodShopEntry(DNFItems.FLYING_KNIFE.getDefaultInstance(), BLOOD_PRICE,
                "item.starrailexpress.dnf_flying_knife"));
        shop.add(new BloodShopEntry(DNFItems.LOCKPICK.getDefaultInstance(), BLOOD_PRICE,
                "item.starrailexpress.dnf_lockpick"));
        ShopContent.customEntries.put(KILLER_ID, shop);
    }

    private static class BloodShopEntry extends ShopEntry {
        private final int bloodPrice;
        private final String itemNameKey;

        BloodShopEntry(ItemStack stack, int bloodPrice, String itemNameKey) {
            super(stack, 0, Type.TOOL);
            this.bloodPrice = bloodPrice;
            this.itemNameKey = itemNameKey;
        }

        @Override
        public boolean canBuy(Player player) {
            return player instanceof ServerPlayer && DNF.isDNFKiller(player)
                    && DNFPlayerComponent.KEY.get(player).getBlood() >= bloodPrice;
        }

        @Override
        public boolean onBuy(Player player) {
            return player instanceof ServerPlayer serverPlayer
                    && DNFBloodPurchaseItem.buy(serverPlayer, bloodPrice, stack().copy(), itemNameKey);
        }
    }
}
