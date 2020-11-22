package net.quartz.pickaxe.mixin;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataCache;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(BlockListProvider.class)
public class BlockListProviderMixin {

	@Mutable
	@Shadow @Final private static Gson GSON;
	private static int i = 0;

	@Redirect(at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Boolean;)V", ordinal = 0), method = "run")
	private void addProperty(JsonObject jsonObject, String property, Boolean value) {

	}

	@Inject(at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Boolean;)V", ordinal = 0), method = "run", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void run(DataCache cache, CallbackInfo ci, JsonObject jsonObject, Iterator var3, Block block, Identifier identifier, JsonObject jsonObject2, StateManager stateManager, JsonArray jsonArray2, UnmodifiableIterator var9, BlockState blockState, JsonObject jsonObject4, JsonObject var15, String var16, Boolean var17) {
		jsonObject2.addProperty("default", Block.getRawIdFromState(blockState));
		jsonObject2.addProperty("interm_id", i);
		i++;
		GSON = (new GsonBuilder()).create();
	}
}
