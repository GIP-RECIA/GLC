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

import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.ldap.ExternalGroupHelper;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.StructureFromGroup;
import fr.recia.glc.ldap.StructureKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author GIP RECIA - Julien Gribonvald 3 juin. 2015
 */
@Slf4j
public class LdapGroupStructureContextMapper implements ContextMapper<IStructure> {

  private ExternalGroupHelper externalGroupHelper;

  /**
   * @param externalGroupHelper
   */
  public LdapGroupStructureContextMapper(ExternalGroupHelper externalGroupHelper) {
    super();
    this.externalGroupHelper = externalGroupHelper;
  }

  @Override
  public StructureFromGroup mapFromContext(Object ctx) throws NamingException {
    Assert.notNull(externalGroupHelper, "The externalGroupHelper should not be null !");
    DirContextAdapter context = (DirContextAdapter) ctx;
    StructureFromGroup structure = new StructureFromGroup();

    Set<String> types = Arrays.stream(context.getStringAttributes("ObjectClass")).collect(Collectors.toSet());
    CategorieStructure cat = null;
    if (types.contains("ENTCollLoc")) {
      cat = CategorieStructure.Collectivite_locale;
    } else if (types.contains("ENTEtablissement")) {
      cat = CategorieStructure.Etablissement;
    } else if (types.contains("ENTServAc")) {
      cat = CategorieStructure.Service_academique;
    } else if (types.contains("ENTEntreprise")) {
      cat = CategorieStructure.Entreprise;
    } else throw new IllegalArgumentException(String.format("Types inconnus from {}", types));

    final String groupId = context.getStringAttribute(externalGroupHelper.getGroupIdAttribute());

    structure.setStructureKey(new StructureKey(groupId, cat));

    // extract infos from pattern
    // récupération du groupe 1 pour la branche, le groupe 3 à split pour le displayName et l'UAI, le groupe 3 pour avoir le nom du groupe de l'établissement
    // Attention tester Branche == coll car groupe 6 vide dans ce cas, et renseigné obligatoirement sinon
    Matcher matcher = externalGroupHelper.getStructureFromGroupPattern().matcher(groupId);
    if (matcher.find() && matcher.groupCount() >= 4) {
      structure.setGroupBranch(matcher.group(1));
      structure.setDisplayName(matcher.group(4));
      if (matcher.groupCount() == 6)
        structure.setUAI(matcher.group(6));
    }

    return structure;
  }

  public ExternalGroupHelper getLdapGroupHelper() {
    return externalGroupHelper;
  }

  public void setLdapGroupHelper(ExternalGroupHelper externalGroupHelper) {
    this.externalGroupHelper = externalGroupHelper;
  }

}
