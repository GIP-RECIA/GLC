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
package fr.recia.glc.security.cas;

import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.security.admingroup.DroitApplicatif;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.NotNull;

public interface IPermissionService {

  DroitApplicatif getRoleOfUserInContext(Authentication authentication, @NotNull final StructureKey contextKey);

//  Predicate filterAuthorizedAllOfContextType(Authentication authentication, @NotNull final ContextType contextType,
//                                             @NotNull final DroitApplicatif permissionType, @NotNull final Predicate predicate);
//
//  Predicate filterAuthorizedChildsOfContext(Authentication authentication, @NotNull final StructureKey contextKey,
//                                            @NotNull final DroitApplicatif permissionType, @NotNull final Predicate predicate);



}
