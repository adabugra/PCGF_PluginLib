/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, JavaPlugin.class, PluginDescriptionFile.class })
public class UpdaterTest
{
	private final static File PLUGINS_FOLDER = new File("plugins"), TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");

	private static PluginDescriptionFile mockedPluginDescription;
	private static String runnableStatus = "";

	private Runnable syncRunnable = new Runnable() {
		@Override
		public void run()
		{
			runnableStatus = "SYNC";
		}
	};

	private Runnable asyncRunnable = new Runnable() {
		@Override
		public void run()
		{
			runnableStatus = "ASYNC";
			try
			{
				Thread.sleep(200);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	};

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		File updateFolder = new File("plugins/updates");
		//noinspection ResultOfMethodCallIgnored
		updateFolder.mkdirs();
		mockStatic(Bukkit.class);
		when(Bukkit.getUpdateFolderFile()).thenReturn(updateFolder);
	}

	@Before
	public void prepareTestObjects() throws Exception
	{
		TestObjects.initMockedJavaPlugin();
		whenNew(at.pcgamingfreaks.Updater.Updater.class).withAnyArguments().thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		suppress(at.pcgamingfreaks.Updater.Updater.class.getDeclaredMethods());
		mockedPluginDescription = PowerMockito.mock(PluginDescriptionFile.class);
		when(TestObjects.getJavaPlugin().getDescription()).thenReturn(mockedPluginDescription);
	}

	@Test
	public void testUpdater() throws Exception
	{
		Updater updater = new Updater(TestObjects.getJavaPlugin(), TARGET_FILE, true, 74734);
		assertNotNull("The updater should not be null", updater);
		updater.runSync(syncRunnable);
		assertEquals("The runnable status text should match", "SYNC", runnableStatus);
		updater.runAsync(asyncRunnable);
		updater.waitForAsyncOperation();
		assertEquals("The runnable status text should match", "ASYNC", runnableStatus);
		Field thread = Updater.class.getDeclaredField("thread");
		thread.setAccessible(true);
		thread.set(updater, null);
		Thread mockedThread = PowerMockito.mock(Thread.class);
		doThrow(new InterruptedException()).when(mockedThread).join();
		whenNew(Thread.class).withArguments(asyncRunnable).thenReturn(mockedThread);
		updater.runAsync(asyncRunnable);
		updater.waitForAsyncOperation();
		assertEquals("No author should be found", "", updater.getAuthor());
		when(mockedPluginDescription.getAuthors()).thenAnswer(new Answer<List<String>>() {
			@Override
			public List<String> answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				List<String> authorList = new ArrayList<>();
				authorList.add("MarkusWME");
				authorList.add("GeorgH93");
				return authorList;
			}
		});
		assertEquals("The author should match", "MarkusWME", updater.getAuthor());
		updater.waitForAsyncOperation();
		thread.set(updater, null);
		updater.waitForAsyncOperation();
		thread.setAccessible(false);
	}

	@AfterClass
	public static void cleanupTestData()
	{
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").delete();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins").delete();
	}
}