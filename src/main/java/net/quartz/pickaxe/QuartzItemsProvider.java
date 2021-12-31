package net.quartz.pickaxe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;

public class QuartzItemsProvider implements DataProvider {

	private final DataGenerator root;
	private static final boolean DEBUG = false;
	private static final Gson GSON = DEBUG ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();

	public QuartzItemsProvider(DataGenerator generator) {
		root = generator;
	}

	@Override
	public void run(DataCache cache) throws IOException {

		JsonObject json = new JsonObject();

		for (Item currItem : Registry.ITEM) {
			JsonObject itemJson = new JsonObject();

			itemJson.addProperty("stack_size", currItem.getMaxCount());
			itemJson.addProperty("item_rarity", String.valueOf(new ItemStack(currItem).getRarity()));
			itemJson.addProperty("enchantability", currItem.getEnchantability());
			itemJson.addProperty("fireproof", currItem.isFireproof());
			itemJson.addProperty("can_be_nested", currItem.canBeNested());


			if (currItem.isFood()) {
				JsonObject foodObject = new JsonObject();
				FoodComponent fc = currItem.getFoodComponent();

				assert fc != null;
				foodObject.addProperty("hunger", fc.getHunger());
				foodObject.addProperty("saturation", fc.getSaturationModifier());
				foodObject.addProperty("meat", fc.isMeat());
				foodObject.addProperty("always_edible", fc.isAlwaysEdible());
				foodObject.addProperty("snack", fc.isSnack());

				JsonArray statusEffects = new JsonArray();

				for(Pair<StatusEffectInstance, Float> statusEffect : fc.getStatusEffects()) {
					JsonObject currEffect = new JsonObject();

					// Maybe find a way to parse the translation key?
					// if it's an uln then we're fine but if its not then we'll probably want to translate it and then convert to snake case
					currEffect.addProperty("name", statusEffect.getFirst().getTranslationKey());
					currEffect.addProperty("duration", statusEffect.getFirst().getDuration());
					currEffect.addProperty("level", statusEffect.getFirst().getAmplifier());
					currEffect.addProperty("chance", statusEffect.getSecond());

					statusEffects.add(currEffect);
				}

				foodObject.add("status_effect", statusEffects);

				itemJson.add("info", foodObject);
			} else if (currItem instanceof ToolItem tool) {
				JsonObject toolObject = new JsonObject();
				if (tool instanceof PickaxeItem) {
					toolObject.addProperty("tool_type", "pickaxe");
					getToolInfo((PickaxeItem) tool, toolObject);
				} else if (tool instanceof SwordItem) {
					toolObject.addProperty("tool_type", "sword");
					getToolInfo((SwordItem) tool, toolObject);
				} else if (tool instanceof ShovelItem) {
					toolObject.addProperty("tool_type", "shovel");
					getToolInfo((ShovelItem) tool, toolObject);
				} else if (tool instanceof AxeItem) {
					toolObject.addProperty("tool_type", "axe");
					getToolInfo((AxeItem) tool, toolObject);
				} else if (tool instanceof HoeItem) {
					toolObject.addProperty("tool_type", "hoe");
					getToolInfo((HoeItem) tool, toolObject);
				}

				itemJson.add("info", toolObject);
			} else if (currItem instanceof ArmorItem armorItem) {
				JsonObject armorJson = new JsonObject();

				switch (armorItem.getSlotType()) {
					case HEAD -> armorJson.addProperty("armor_type", "helmet");
					case CHEST -> armorJson.addProperty("armor_type", "chestplate");
					case LEGS -> armorJson.addProperty("armor_type", "leggings");
					case FEET -> armorJson.addProperty("armor_type", "boots");
				}

				armorJson.addProperty("protection", armorItem.getProtection());
				armorJson.addProperty("toughness", armorItem.getToughness());
				armorJson.addProperty("max_durability", armorItem.getMaxDamage());

				itemJson.add("info", armorJson);
			} else if (currItem instanceof ShearsItem) addUsableInfo(currItem, itemJson, "shears");
			else if (currItem instanceof FlintAndSteelItem) addUsableInfo(currItem, itemJson, "flint_and_steel");
			else if (currItem instanceof OnAStickItem onAStickItem) {
				if (onAStickItem == Items.CARROT_ON_A_STICK) addUsableInfo(onAStickItem, itemJson, "carrot_stick");
				else addUsableInfo(onAStickItem, itemJson, "fungus_stick");
			} else if (currItem instanceof RangedWeaponItem) {
				JsonObject rangedJson = new JsonObject();
				if (currItem instanceof BowItem bow) {
					rangedJson.addProperty("weapon_type", "bow");
					rangedJson.addProperty("max_charge_time", bow.getMaxUseTime(ItemStack.EMPTY));
					rangedJson.addProperty("max_durability", bow.getMaxDamage());
				} else if (currItem instanceof CrossbowItem crossbow) {
					rangedJson.addProperty("weapon_type", "crossbow");
					rangedJson.addProperty("max_charge_time", crossbow.getMaxUseTime(ItemStack.EMPTY));
					rangedJson.addProperty("max_durability", crossbow.getMaxDamage());
				}

				itemJson.add("info", rangedJson);
			} else if (currItem instanceof TridentItem tridentItem) {
				JsonObject rangedJson = new JsonObject();

				rangedJson.addProperty("weapon_type", "trident");
				rangedJson.addProperty("max_charge_time", tridentItem.getMaxUseTime(ItemStack.EMPTY));
				rangedJson.addProperty("max_durability", tridentItem.getMaxDamage());

				itemJson.add("info", rangedJson);
			}

			json.add(currItem.toString(), itemJson);
		}

		Path outputPath = root.getOutput().resolve("reports/items.json");
		DataProvider.writeToPath(GSON, cache, json, outputPath);

	}

	private <T extends MiningToolItem> void getToolInfo(T item, JsonObject obj) {
		obj.addProperty("attack_damage", item.getAttackDamage());
		obj.addProperty("level", item.getMaterial().toString().toLowerCase());
		// Why tf do I have to do this mojang
		EntityAttributeModifier[] attackSpeed = item.getAttributeModifiers(EquipmentSlot.MAINHAND)
				.get(EntityAttributes.GENERIC_ATTACK_SPEED)
				.toArray(EntityAttributeModifier[]::new);

		obj.addProperty("attack_speed", attackSpeed[0].getValue() + 4.0);
	}

	private <T extends SwordItem> void getToolInfo(T item, JsonObject obj) {
		obj.addProperty("attack_damage", item.getAttackDamage());
		obj.addProperty("level", item.getMaterial().toString().toLowerCase());
		// Why tf do I have to do this mojang
		EntityAttributeModifier[] attackSpeed = item.getAttributeModifiers(EquipmentSlot.MAINHAND)
				.get(EntityAttributes.GENERIC_ATTACK_SPEED)
				.toArray(EntityAttributeModifier[]::new);

		obj.addProperty("attack_speed", attackSpeed[0].getValue() + 4.0);
	}

	private void addUsableInfo(Item item, JsonObject obj, String usableType) {
		JsonObject usableObj = new JsonObject();

		usableObj.addProperty("usable_type", usableType);
		usableObj.addProperty("max_durability", item.getMaxDamage());

		obj.add("info", usableObj);
	}


	@Override
	public String getName() {
		return "Quartz Item Provider";
	}
}
