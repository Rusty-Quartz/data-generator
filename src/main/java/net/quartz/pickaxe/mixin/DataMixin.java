package net.quartz.pickaxe.mixin;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.Main;
import net.quartz.pickaxe.QuartzItemsProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Collection;

@Mixin(Main.class)
public class DataMixin {
	@Inject(method = "create*", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	private static void create(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, CallbackInfoReturnable<DataGenerator> cir, DataGenerator dataGenerator) {
		if(includeReports) {
			dataGenerator.addProvider(new QuartzItemsProvider(dataGenerator));
		}
	}
}
