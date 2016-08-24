/*
 * Copyright (C) 2014-2015 MarkusWME
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class LanguageUpdateMethodTest
{
	@Test
	public void testLanguageUpdateMethods()
	{
		assertNotNull("The update method should be found", LanguageUpdateMethod.UPDATE);
		assertNotNull("The upgrade method should be found", LanguageUpdateMethod.UPGRADE);
		assertNotNull("The overwrite method should be found", LanguageUpdateMethod.OVERWRITE);
	}
}