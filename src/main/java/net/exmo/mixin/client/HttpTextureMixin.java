package net.exmo.mixin.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(SkinManager.class)
public class HttpTextureMixin  {

    private static UUID uuid;
    @Inject(method = "registerTextures", at = @At(value = "HEAD"))
    public void registerTextures(UUID uUID, MinecraftProfileTextures minecraftProfileTextures, CallbackInfoReturnable<CompletableFuture<PlayerSkin>> cir) {
        uuid = uUID;
    }


    @Redirect(method = "registerTextures", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftProfileTextures;skin()Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;"))
    public MinecraftProfileTexture redirectSkin(MinecraftProfileTextures instance) {
        AtomicReference<MinecraftProfileTexture> skin = new AtomicReference<>(instance.skin());
        if (skin.get() == null) {

            AtomicBoolean found = new AtomicBoolean(false);
            Minecraft.getInstance().getConnection().getOnlinePlayers().forEach(player -> {
                if (!found.get()){
                if (player.getProfile().getId().equals(uuid)) {
                    HashMap<String, String> metadata = new HashMap<>();
                    metadata.put("model", "slim");
                    skin.set(new MinecraftProfileTexture("https://littleskin.cn/skin/" + player.getProfile().getName() + ".png", metadata));
                    found.set(true);
                }
                }
            });
        }
        return skin.get();
    }
}
