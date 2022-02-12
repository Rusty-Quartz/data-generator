package net.quartz.pickaxe.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.HashCache;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(BlockListReport.class)
public class BlockListProviderMixin {

	@Mutable
	@Shadow @Final private static Gson GSON;
	private static int i = 0;



	@Inject(at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", ordinal = 4), method = "run", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void run(HashCache cache, CallbackInfo ci, JsonObject jsonObject, Iterator var3, Block block, ResourceLocation resourceLocation, JsonObject jsonObject2, StateDefinition stateDefinition, JsonArray jsonArray2) {
		jsonObject2.addProperty("default", Block.getId(block.defaultBlockState()));
		jsonObject2.addProperty("interm_id", i);
		i++;
		GSON = (new GsonBuilder()).create();
	}
}
