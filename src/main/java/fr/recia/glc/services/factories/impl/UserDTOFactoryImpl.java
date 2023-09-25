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
import fr.recia.glc.ldap.IExternalUser;
import fr.recia.glc.ldap.repository.IExternalUserDao;
import fr.recia.glc.services.factories.UserDTOFactory;
import fr.recia.glc.web.dto.UserDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserDTOFactoryImpl implements UserDTOFactory {

  @Inject
  @Getter
  private transient APersonneRepository dao;

  @Inject
  @Getter
  private transient IExternalUserDao extDao;

  public UserDTOFactoryImpl() {
    super();
  }

  public UserDTO from(final APersonne model) {
    log.debug("Model to DTO of {}", model);
    IExternalUser extUser = getExtDao().getUserByUid(model.getUid());
    return from(model, extUser);
  }

  public UserDTO from(final String id) {
    log.debug("from login Id to DTO of {}", id);
    IExternalUser extUser = getExtDao().getUserByUid(id);
    return from(extUser, true);
  }

  @Override
  public UserDTO from(final IExternalUser extModel, boolean withInternal) {
    log.debug("External to DTO of {}", extModel);
    APersonne model = null;
    if (extModel != null && withInternal) {
      Optional<APersonne> optionalAPersonne = dao.findOne(QAPersonne.aPersonne.uid.eq(extModel.getId()));
      model = optionalAPersonne.orElse(null);
    }
    return from(model, extModel);
  }

  @Override
  public UserDTO from(final APersonne model, final IExternalUser extModel) {
    if (model != null && extModel != null) {
      return new UserDTO(model.getUid(), extModel.getDisplayName(),
        extModel.getEmail(), extModel.getAttributes());
    } else if (model != null) {
      return new UserDTO(model.getUid(), model.getDisplayName());
    } else if (extModel != null) {
      return new UserDTO(extModel.getId(), extModel.getDisplayName(),
        extModel.getEmail(), extModel.getAttributes());
    }
    return null;
  }

  public APersonne from(final UserDTO dtObject) {
    log.debug("DTO to model of {}", dtObject);
    if (dtObject != null) {
      Optional<APersonne> optionalUser = dao.findOne(QAPersonne.aPersonne.uid.eq(dtObject.getUserId()));
      APersonne user = optionalUser.orElse(null);
      if (user == null) {
        return null;
      }
      return user;
    }
    return null;
  }

  public List<UserDTO> asDTOList(final Collection<APersonne> models) {
    final List<UserDTO> tos = Lists.newLinkedList();

    if ((models != null) && !models.isEmpty()) {
      for (APersonne model : models) {
        tos.add(from(model));
      }
    }

    return tos;
  }

  public List<UserDTO> asDTOList(final Collection<IExternalUser> models,
                                 boolean withInternal) {
    final List<UserDTO> tos = Lists.newLinkedList();

    if ((models != null) && !models.isEmpty()) {
      for (IExternalUser model : models) {
        tos.add(from(model, withInternal));
      }
    }

    return tos;
  }

  public Set<UserDTO> asDTOSet(final Collection<APersonne> models) {
    final Set<UserDTO> dtos = Sets.newLinkedHashSet();
    for (APersonne model : models) {
      dtos.add(from(model));
    }
    return dtos;
  }

  public Set<UserDTO> asDTOSet(final Collection<IExternalUser> models,
                               boolean withInternal) {
    final Set<UserDTO> tos = Sets.newLinkedHashSet();

    if ((models != null) && !models.isEmpty()) {
      for (IExternalUser model : models) {
        tos.add(from(model, withInternal));
      }
    }

    return tos;
  }

  public Set<APersonne> asSet(final Collection<UserDTO> dtObjects) {
    final Set<APersonne> models = Sets.newLinkedHashSet();
    for (UserDTO dtObject : dtObjects) {
      models.add(from(dtObject));
    }
    return models;
  }

  // @Override
  // public SimplePersonneDto from(final String id) {
  // return getDao().findOne(id);
  // }

}
