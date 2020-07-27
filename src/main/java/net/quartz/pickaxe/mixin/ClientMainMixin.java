package net.quartz.pickaxe.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import org.apache.logging.log4j.LogManager;

@Environment(EnvType.CLIENT)
@Mixin(value = Main.class, remap = false)
public class ClientMainMixin {

	@Overwrite
	public static void main(String[] args) {
		LogManager.getLogger("data-generator").info("Redirecting to data generators");
		try {
			net.minecraft.data.Main.main(new String[]{"--reports"});
		} catch(Exception e) {
			System.out.println("Failed to run the data main");
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
