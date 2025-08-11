package com.nosferk.ragequit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RageQuitClient implements ClientModInitializer {
	private static KeyBinding rageQuitKey;

	@Override
	public void onInitializeClient() {
		rageQuitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.rage-quit.quit",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_APOSTROPHE,
			"category.rage-quit.general"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (rageQuitKey.wasPressed()) {
				performRageQuit();
			}
		});
	}

	private void performRageQuit() {
		MinecraftClient client = MinecraftClient.getInstance();
		
		if (client.world != null) {
			TitleScreen titleScreen = new TitleScreen();
			
			try {
				if (client.getNetworkHandler() != null) {
					client.getNetworkHandler().getConnection().disconnect(Text.literal("Rage Quit"));
				}
				
				if (client.isInSingleplayer()) {
					client.disconnect(titleScreen, false);
				} else {
					client.disconnect(new MultiplayerScreen(titleScreen), false);
				}
				
			} catch (Exception e) {
				RageQuit.LOGGER.error("Error during rage quit, forcing screen change: " + e.getMessage());
				
				if (client.isInSingleplayer()) {
					client.setScreen(titleScreen);
				} else {
					client.setScreen(new MultiplayerScreen(titleScreen));
				}
			}
			
			RageQuit.LOGGER.info("Rage quit activated! Disconnected from server/world using apostrophe key.");
		}
	}
}