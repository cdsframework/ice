/*
 * Copyright 2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.plugin.opioid.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * None of the {@link Consumer}s returned by these methods is purely functional;
 * they mutate the method argument.
 * 
 * @author phillip
 *
 */
public class ConsumerUtil {

	/**
	 * Iterates through a {@link ResultSet} and inserts data into a
	 * {@link Map&lt;Integer, String&gt;}.
	 * 
	 * @param map
	 * @return
	 */
	public static Consumer<ResultSet> getConsumerIntString(Map<Integer, String> map) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					map.put(Integer.valueOf(rs.getInt(1)), rs.getString(2));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}

	public static Consumer<ResultSet> getConsumerIntInt(Map<Integer, Integer> map) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					map.put(Integer.valueOf(rs.getInt(1)), Integer.valueOf(rs.getInt(2)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}

	public static Consumer<ResultSet> getConsumerIntDouble(Map<Integer, Double> map) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					map.put(Integer.valueOf(rs.getInt(1)), Double.valueOf(rs.getDouble(2)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}
	
	public static Consumer<ResultSet> getConsumerIntSetInt(Map<Integer, Set<Integer>> map) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					Integer key = Integer.valueOf(rs.getInt(1));
					Integer value = Integer.valueOf(rs.getInt(2));
					map.compute(key, (k, v) -> {
						if (v == null) {
							v = new HashSet<>();
							v.add(value);
						}
						return v;
					});
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}
	
	public static Consumer<ResultSet> getConsumerInt(Set<Integer> set) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					set.add(Integer.valueOf(rs.getInt(1)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}

	public static Consumer<ResultSet> getConsumerString(Set<String> set) {
		return (ResultSet rs) -> {
			try {
				while (rs.next()) {
					set.add(rs.getString(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}

	
}
