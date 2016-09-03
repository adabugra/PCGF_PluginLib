/*
 *   Copyright (C) 2016 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;
import at.pcgamingfreaks.Message.MessageHoverEvent;

import com.google.gson.*;

import org.bukkit.*;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class MessageComponent extends at.pcgamingfreaks.Message.MessageComponent<MessageComponent> implements JsonDeserializer<MessageComponent>
{
	//region Reflection Variables
	private transient final static Class<?> CRAFT_STATISTIC = NMSReflection.getOBCClass("CraftStatistic");
	private transient final static Method GET_NMS_ACHIEVEMENT =  NMSReflection.getMethod(CRAFT_STATISTIC, "getNMSAchievement", Achievement.class);
	private transient final static Method GET_NMS_STATISTIC = NMSReflection.getMethod(CRAFT_STATISTIC, "getNMSStatistic", Statistic.class);
	private transient final static Method GET_MATERIAL_STATISTIC = NMSReflection.getMethod(CRAFT_STATISTIC, "getMaterialStatistic", Statistic.class, Material.class);
	private transient final static Method GET_ENTITY_STATISTIC = NMSReflection.getMethod(CRAFT_STATISTIC, "getEntityStatistic", Statistic.class, EntityType.class);
	private transient final static Field FIELD_STATISTIC_NAME = NMSReflection.getField(NMSReflection.getNMSClass("Statistic"), "name");
	//endregion

	private transient final static at.pcgamingfreaks.Message.MessageComponent NEW_LINE_HELPER = new MessageComponent("\n");
	private transient final static MessageComponent MESSAGE_COMPONENT_INSTANCE = new MessageComponent();

	static
	{
		GSON = new GsonBuilder().registerTypeAdapter(at.pcgamingfreaks.Message.MessageComponent.class, MESSAGE_COMPONENT_INSTANCE).create();
		try
		{
			messageComponentClass = MessageComponent.class;
			messageComponentConstructor = MessageComponent.class.getConstructor();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected at.pcgamingfreaks.Message.MessageComponent getNewLineComponent()
	{
		return NEW_LINE_HELPER;
	}

	//region Constructors
	/**
	 * Creates a new empty MessageComponent instance.
	 */
	public MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the MessageComponent.
	 * @param styles The style for the MessageComponent.
	 */
	public MessageComponent(String text, ChatColor... styles)
	{
		super(text);
		setStyles(styles);
	}
	//endregion

	//region Deserializer and Deserializer Functions
	/**
	 * Generates a MessageComponent list from a given JSON string.
	 *
	 * @param jsonString The JSON string representing the components.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	public static List<MessageComponent> fromJson(String jsonString)
	{
		return MESSAGE_COMPONENT_INSTANCE.fromJsonWorker(jsonString);
	}

	/**
	 * Generates a MessageComponent list from a given {@link JsonArray} object.
	 *
	 * @param componentArray The {@link JsonArray} containing all the components, from the deserializer.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	public static List<MessageComponent> fromJsonArray(JsonArray componentArray)
	{
		return MESSAGE_COMPONENT_INSTANCE.fromJsonArrayWorker(componentArray);
	}
	//endregion

	//region Message modifier (getter/setter).
	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link ChatColor}, null if no color is defined.
	 */
	public ChatColor getChatColor()
	{
		return ChatColor.valueOf(getColorString().toUpperCase());
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageComponent setColor(ChatColor color) throws IllegalArgumentException
	{
		setColor(color.name().toUpperCase());
		return this;
	}

	/**
	 * Sets the formats of the component.
	 *
	 * @param formats The array of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	@SuppressWarnings("Duplicates")
	public MessageComponent setFormats(ChatColor... formats) throws IllegalArgumentException
	{
		if(formats != null)
		{
			for(ChatColor style : formats)
			{
				if(!style.isFormat())
				{
					throw new IllegalArgumentException(style.name() + " is not a formatter");
				}
			}
			for(ChatColor format : formats)
			{
				switch(format)
				{
					case ITALIC: setItalic(); break;
					case BOLD: setBold(); break;
					case UNDERLINE: setUnderlined(); break;
					case STRIKETHROUGH: setStrikethrough(); break;
					case MAGIC: setObfuscated(); break;
				}
			}
		}
		return this;
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public MessageComponent setStyles(ChatColor... styles)
	{
		if(styles != null)
		{
			for(ChatColor style : styles)
			{
				if(style.isFormat())
				{
					setFormats(style);
				}
				if(style.isColor())
				{
					setColor(style);
				}
			}
		}
		return this;
	}
	//endregion

	//region Short message modifier (setter)
	/**
	 * Sets the format of the component
	 *
	 * @param formats The array of format to apply to the component
	 * @return This message component instance
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters
	 */
	public MessageComponent format(ChatColor... formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public MessageComponent style(ChatColor... styles)
	{
		return setStyles(styles);
	}

	/**
	 * Set the behavior of the component to display information about an achievement when the client hovers over the text.
	 *
	 * @param achievement The achievement to display.
	 * @return This message component instance.
	 */
	public MessageComponent achievementTooltip(Achievement achievement)
	{
		try
		{
			return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, (String) FIELD_STATISTIC_NAME.get(GET_NMS_ACHIEVEMENT.invoke(null, achievement)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied.
	 */
	public MessageComponent statisticTooltip(Statistic statistic) throws IllegalArgumentException
	{
		Type type = statistic.getType();
		if (type != Type.UNTYPED) throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
		try
		{
			return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, (String) FIELD_STATISTIC_NAME.get(GET_NMS_STATISTIC.invoke(null, statistic)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param material The sole material parameter to the statistic.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageComponent statisticTooltip(Statistic statistic, Material material) throws IllegalArgumentException
	{
		Type type = statistic.getType();
		if (type == Type.UNTYPED) throw new IllegalArgumentException("That statistic needs no additional parameter!");
		if ((type == Type.BLOCK && material.isBlock()) || type == Type.ENTITY) throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		try
		{
			return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, (String) FIELD_STATISTIC_NAME.get(GET_MATERIAL_STATISTIC.invoke(null, statistic, material)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param entity The sole entity type parameter to the statistic.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageComponent statisticTooltip(Statistic statistic, EntityType entity) throws IllegalArgumentException
	{
		Type type = statistic.getType();
		if (type == Type.UNTYPED) throw new IllegalArgumentException("That statistic needs no additional parameter!");
		if (type != Type.ENTITY) throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		try
		{
			return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, (String) FIELD_STATISTIC_NAME.get(GET_ENTITY_STATISTIC.invoke(null, statistic, entity)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set the behavior of the component to display information about an item when the client hovers over the text.
	 *
	 * @param itemStack The stack for which to display information.
	 * @return This message component instance.
	 */
	public MessageComponent itemTooltip(ItemStack itemStack)
	{
		return itemTooltip(Utils.convertItemStackToJson(itemStack, Bukkit.getLogger()));
	}
	//endregion
}