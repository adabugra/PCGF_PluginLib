/*
 *   Copyright (C) 2022 GeorgH93
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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IUpdater
{
	void checkForUpdate(final @Nullable UpdateResponseCallback response);
	default void update() { update(null); }
	void update(final @Nullable UpdateResponseCallback response);
	void update(final @NotNull UpdateMode updateMode, final @Nullable UpdateResponseCallback response);

	/**
	 * Waits for the async worker to finish.
	 * We need to prevent the server from closing while we still work.
	 */
	void waitForAsyncOperation();
	boolean isRunning();
}