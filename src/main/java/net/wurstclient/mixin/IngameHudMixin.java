/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.wurstclient.WurstClient;
import net.wurstclient.event.EventManager;
import net.wurstclient.events.GUIRenderListener.GUIRenderEvent;

@Mixin(ForgeIngameGui.class)
public class IngameHudMixin extends DrawableHelper
{
	@Inject(
		at = @At("RETURN"),
		method = "render")
	private void onRender(MatrixStack matrixStack, float partialTicks,
		CallbackInfo ci)
	{
		if(WurstClient.MC.options.debugEnabled)
			return;
		
		GUIRenderEvent event = new GUIRenderEvent(matrixStack, partialTicks);
		EventManager.fire(event);
	}
	
//	@Inject(at = {@At("HEAD")},
//		method = {"renderPumpkinOverlay()V"},
//		cancellable = true)
//	private void onRenderPumpkinOverlay(CallbackInfo ci)
//	{
//		if(WurstClient.INSTANCE.getHax().noPumpkinHack.isEnabled())
//			ci.cancel();
//	}
}
