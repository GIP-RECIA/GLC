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

import fr.recia.glc.ldap.IExternalGroup;
import fr.recia.glc.ldap.StructureFromGroup;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 11 juil. 2014
 */
public interface IExternalGroupDao {

  Set<StructureFromGroup> getStructuresFromGroups();

  List<IExternalGroup> getGroupsWithFilter(@NotNull final String stringFilter, final String token, final boolean withMembers);

}
