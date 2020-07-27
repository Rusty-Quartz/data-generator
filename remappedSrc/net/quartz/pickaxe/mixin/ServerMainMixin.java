package net.quartz.pickaxe.mixin;

import net.minecraft.server.Main;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Main.class, remap = false)
public class ServerMainMixin {

	@Overwrite
	public static void main(String[] args) {
		LogManager.getLogger("data-generator").info("Redirecting to data generators");
		try {
			net.minecraft.data.Main.main(new String[]{"--all"});
		} catch(Exception e) {
			System.out.println("Failed to run the data main");
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
