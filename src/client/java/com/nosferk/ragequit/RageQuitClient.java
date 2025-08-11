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
		// Registrar o keybinding para a tecla Aspas (')
		rageQuitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.rage-quit.quit", // nome da tradução
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_APOSTROPHE, // tecla aspas (')
			"category.rage-quit.general" // categoria
		));

		// Registrar o evento de tick para verificar se a tecla foi pressionada
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (rageQuitKey.wasPressed()) {
				// Executar rage quit diretamente quando a tecla for pressionada
				performRageQuit();
			}
		});
	}

	private void performRageQuit() {
		MinecraftClient client = MinecraftClient.getInstance();
		
		// Se estiver em um mundo/servidor
		if (client.world != null) {
			// Primeiro, tentar desconectar usando o método oficial
			TitleScreen titleScreen = new TitleScreen();
			
			try {
				// Forçar desconexão real do servidor/mundo
				if (client.getNetworkHandler() != null) {
					// Se estiver conectado a um servidor, desconectar da rede
					client.getNetworkHandler().getConnection().disconnect(Text.literal("Rage Quit"));
				}
				
				// Usar o método de desconexão oficial do Minecraft
				if (client.isInSingleplayer()) {
					// Se for singleplayer, desconecta e volta para o menu principal
					client.disconnect(titleScreen, false);
				} else {
					// Se for multiplayer, desconecta e volta para a lista de servidores
					client.disconnect(new MultiplayerScreen(titleScreen), false);
				}
				
			} catch (Exception e) {
				// Se houver erro na desconexão, forçar a mudança de tela
				RageQuit.LOGGER.error("Error during rage quit, forcing screen change: " + e.getMessage());
				
				if (client.isInSingleplayer()) {
					client.setScreen(titleScreen);
				} else {
					client.setScreen(new MultiplayerScreen(titleScreen));
				}
			}
			
			// Log da ação
			RageQuit.LOGGER.info("Rage quit activated! Disconnected from server/world using apostrophe key.");
		}
	}
}