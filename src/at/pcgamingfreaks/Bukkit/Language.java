/*
 *   Copyright (C) 2014-2016 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod;
import at.pcgamingfreaks.Reflection;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Language extends at.pcgamingfreaks.Language
{
	protected final JavaPlugin plugin;

	static
	{
		messageClasses = new MessageClassesReflectionDataHolder(Reflection.getConstructor(Message.class, String.class), Reflection.getMethod(Message.class, "setSendMethod", SendMethod.class),
		                                                        Reflection.getMethod(SendMethod.class, "getMetadataFromJsonMethod"), SendMethod.class);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 */
	public Language(JavaPlugin plugin, int version)
	{
		this(plugin, version, -1);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(JavaPlugin plugin, int version, String path, String prefix)
	{
		this(plugin, version, -1, path, prefix);
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 */
	public Language(JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, File.separator + "lang", "");
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 */
	public Language(JavaPlugin plugin, int version, int upgradeThreshold, String path, String prefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, prefix, "");
		this.plugin = plugin;
	}

	/**
	 * Gets the message from the language file and replaces bukkit color codes (&) to minecraft color codes (§)
	 *
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	@Override
	public String getTranslated(String path)
	{
		return ChatColor.translateAlternateColorCodes('&', get(path));
	}

	public Message getMessage(String path)
	{
		return getMessage(path, true);
	}

	public Message getMessage(String path, boolean escapeStringFormatCharacters)
	{
		if(NMSReflection.getVersion().contains("1_7"))
		{
			Message msg = new Message((escapeStringFormatCharacters) ? getTranslated(path).replaceAll("%", "%%") : getTranslated(path));
			msg.setSendMethod(SendMethod.CHAT_CLASSIC);
			return msg;
		}
		else
		{
			return super.getMessage(escapeStringFormatCharacters, path);
		}
	}
}