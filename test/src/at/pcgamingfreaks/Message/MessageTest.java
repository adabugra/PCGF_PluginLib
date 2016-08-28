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

package at.pcgamingfreaks.Message;

import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageTest
{
	private static class TestMessageComponent extends MessageComponent<MessageComponent>
	{
		private static final MessageComponent MESSAGE_COMPONENT_INSTANCE = new TestMessageComponent();

		static
		{
			GSON = new GsonBuilder().registerTypeAdapter(TestMessageComponent.class, new TestMessageComponent()).create();
			messageComponentClass = TestMessageComponent.class;
			try
			{
				messageComponentConstructor = TestMessageComponent.class.getConstructor();
			}
			catch(NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}

		public TestMessageComponent() {}

		public TestMessageComponent(String text)
		{
			super(text);
		}

		@Override
		protected MessageComponent getNewLineComponent()
		{
			return new TestMessageComponent("\n");
		}

		public static List<MessageComponent> fromJson(String jsonString)
		{
			//noinspection unchecked
			return MESSAGE_COMPONENT_INSTANCE.fromJsonWorker(jsonString);
		}
	}

	private static class TestMessage extends Message<Message>
	{
		public TestMessage()
		{
			super("", MessageComponent.class);
		}

		public TestMessage(String message)
		{
			super(message, TestMessageComponent.class);
		}

		protected TestMessage(Collection<? extends MessageComponent> message)
		{
			super(message);
		}
	}

	@Test
	public void testMessage()
	{
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent("Message text"));
		TestMessage message1 = new TestMessage("[{\"text\":\"Message text\"}]");
		TestMessage message2 = new TestMessage("Message text§r");
		TestMessage message3 = new TestMessage(messageComponents);
		message2.setOptionalParameters(5);
		assertEquals("The message texts should match", message1.getClassicMessage(), message2.getClassicMessage());
		assertTrue("The messages should be equal", message1.equals(message3));
		assertEquals("The message hash code should match", message1.hashCode(), message3.hashCode());
		assertEquals("The message text should be correct", "[{\"text\":\"Message text\"}]", message3.toString());
		//noinspection deprecation
		assertEquals("The message components should be equal", messageComponents.toArray(), message3.getMessageComponents());
		assertEquals("The optional parameter of the message should match", 5, message2.optionalParameters);
		message1.replaceAll("ext", "est");
		message2.replaceAll("ext", "est");
		assertEquals("The message texts should match", message1.getClassicMessage(), message2.getClassicMessage());
	}

	@Test
	public void testMessageWithError()
	{
		assertEquals("The test message should be empty", "", new TestMessage().getClassicMessage());
	}
}