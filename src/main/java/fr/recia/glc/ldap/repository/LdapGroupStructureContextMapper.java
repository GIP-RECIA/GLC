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

import com.google.common.collect.Lists;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.ldap.ExternalGroup;
import fr.recia.glc.ldap.ExternalGroupHelper;
import fr.recia.glc.ldap.IExternalGroup;
import fr.recia.glc.ldap.IExternalGroupDisplayNameFormatter;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.StructureFromGroup;
import fr.recia.glc.ldap.StructureKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    StructureFromGroup group = new StructureFromGroup();

    Set<String> types = Arrays.stream(context.getStringAttributes("ObjectClass")).collect(Collectors.toSet());
    CategorieStructure cat = null;
      if (types.contains("ENTCollLoc")) cat = CategorieStructure.Collectivite_locale;
      //TODO "ENTEtablissement", "ENTServAc", "ENTEntreprise"

    if (cat == null) throw new IllegalArgumentException(String.format("Types inconnus from {}", types));

    final String id = context.getStringAttribute(externalGroupHelper.getGroupIdAttribute());

    group.setStructureKey(new StructureKey(id,cat));

    //TODO
    //extract infos from pattern
    // récupération du groupe 1 pour la branche, le groupe 4 pour le displayName, le groupe 6 pour l'UAI, le groupe 3 pour avoir le nom du groupe de l'établissement
    // Attention tester Branche == coll car groupe 6 vide dans ce cas, et renseigné obligatoirement sinon
    externalGroupHelper.getStructureFromGroupPattern();

    group.setDisplayName();
    group.setUAI();
    group.setGroupBranch();
    group.setGroupNameEtab();

    return group;
  }

  public ExternalGroupHelper getLdapGroupHelper() {
    return externalGroupHelper;
  }

  public void setLdapGroupHelper(ExternalGroupHelper externalGroupHelper) {
    this.externalGroupHelper = externalGroupHelper;
  }

}
