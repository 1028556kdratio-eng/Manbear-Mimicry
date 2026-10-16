package com.manbear.client;

import com.manbear.ManbearMod;
import com.manbear.net.ManbearNet;
import com.manbear.net.StrugglePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Reports space-bar presses so you can mash your way out of his grip. */
@Mod.EventBusSubscriber(modid = ManbearMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean notice;

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        MicSampler.start();
        notice = MicSampler.isRunning();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        MicSampler.stop();
        notice = false;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        VoicePlayer.tick();
        Minecraft mc = Minecraft.getInstance();
        if (notice && mc.player != null) {
            notice = false;
            mc.player.displayClientMessage(Component.literal(
                    "[Manbear] Your microphone is being sampled so something out there can learn your voice. "
                    + "Clips stay in memory on this PC only. Turn it off with micSampling=false in manbear-client.toml."), false);
        }
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (event.getAction() == 1 /* GLFW_PRESS */ && mc.options.keyJump.matches(event.getKey(), event.getScanCode())) {
            ManbearNet.CHANNEL.sendToServer(new StrugglePacket());
        }
    }
}
