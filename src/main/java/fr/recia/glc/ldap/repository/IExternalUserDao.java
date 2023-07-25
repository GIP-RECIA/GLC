/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.glc.ldap.repository;

import fr.recia.glc.ldap.IExternalUser;

import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 11 juil. 2014
 */
public interface IExternalUserDao {

  IExternalUser getUserByUid(final String uid);

  List<String> getUserMailsByUids(final Iterable<String> uids);

  List<IExternalUser> getUsersByUids(final Iterable<String> uids);

  List<IExternalUser> getUsersByUidsWithFilter(final Iterable<String> uids, final String token);

  List<IExternalUser> getUsersByGroupId(final String groupId);

  List<IExternalUser> getUsersFromParentGroups(final Iterable<String> groupIds, final String token);

  List<IExternalUser> getUsersWithFilter(final String stringFilter, final String token);

  boolean isUserFoundWithFilter(final String stringFilter, final String uid);

}
