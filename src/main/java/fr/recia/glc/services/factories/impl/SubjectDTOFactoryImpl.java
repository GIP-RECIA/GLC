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
/**
 *
 */
package fr.recia.glc.services.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.QAPersonne;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.ldap.SubjectKey;
import fr.recia.glc.ldap.enums.SubjectType;
import fr.recia.glc.services.exceptions.ObjectNotFoundException;
import fr.recia.glc.services.factories.CompositeKeyDTOFactory;
import fr.recia.glc.services.factories.SubjectDTOFactory;
import fr.recia.glc.web.dto.SubjectDTO;
import fr.recia.glc.web.dto.SubjectKeyDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class SubjectDTOFactoryImpl implements SubjectDTOFactory {

  @Autowired
  @Getter
  private transient APersonneRepository dao;

  @Autowired
  private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

  public SubjectDTOFactoryImpl() {
    super();
  }

  public SubjectDTO from(final APersonne model) {
    log.debug("Model to DTO of {}", model);
    return new SubjectDTO(subjectConverter.convertToDTOKey(model.getSubject()), model.getDisplayName(), true);
  }

  public APersonne from(final SubjectDTO dtObject) throws ObjectNotFoundException {
    log.debug("DTO to Model of {}", dtObject);
    if (dtObject != null && SubjectType.PERSON.equals(dtObject.getModelId().getKeyType())) {
      Optional<APersonne> optionalUser = getDao().findOne(QAPersonne.aPersonne.uid.eq(subjectConverter.convertToModelKey(dtObject.getModelId()).getKeyId()));
      APersonne user = optionalUser.orElse(null);
      if (user == null) {
        throw new ObjectNotFoundException(dtObject.getModelId().getKeyId(), APersonne.class);
      }
      return user;
    }
    return null;
  }

  public List<SubjectDTO> asDTOList(final Collection<APersonne> models) {
    final List<SubjectDTO> tos = Lists.newArrayList();

    if ((models != null) && !models.isEmpty()) {
      for (APersonne model : models) {
        tos.add(from(model));
      }
    }

    return tos;
  }

  public Set<SubjectDTO> asDTOSet(final Collection<APersonne> models) {
    final Set<SubjectDTO> dtos = Sets.newHashSet();
    for (APersonne model : models) {
      dtos.add(from(model));
    }
    return dtos;
  }

  public Set<APersonne> asSet(final Collection<SubjectDTO> dtObjects)
    throws ObjectNotFoundException {
    final Set<APersonne> models = Sets.newHashSet();
    for (SubjectDTO dtObject : dtObjects) {
      models.add(from(dtObject));
    }
    return models;
  }

  public APersonne from(final SubjectKeyDTO id) throws ObjectNotFoundException {
    if (SubjectType.PERSON.equals(id.getKeyType())) {
      Optional<APersonne> optionalUser = getDao().findOne(QAPersonne.aPersonne.uid.eq(subjectConverter.convertToModelKey(id).getKeyId()));
      APersonne user = optionalUser.orElse(null);
      return user;
    }
    return null;
  }

}
