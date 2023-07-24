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
package fr.recia.glc.security.admingroup;

import org.eclipse.jdt.annotation.NonNull;
import org.esco.admingroup.parameters.ParameterKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Les différents droits d'utilisation de l'application.
 *
 * @author legay
 */
public enum DroitApplicatif {
  /**
   * Lecture des incertains modifications des suppressions
   */
  AdminWriter,
  /**
   * Lecture des incertains et suppressions.
   */
  AdminReader,
  /**
   * Exporter les données prédéfinies.
   */
  Export,
  /**
   * Modifier l'état des comptes via les groupes.
   */
  GlobalWriter,
  /**
   * Lire les comptes via les groupes.
   */
  GlobalReader,
  /**
   * Créer des utilisateurs locaux.
   */
  LocalWriter,
  /**
   * Lire les utilisateurs locaux.
   */
  LocalReader,
  /**
   * Idem GlobalWriter
   * mais donne les droit sur les personnes hors établissement de rattachement.
   */
  SuperGlobalWriter;

  private ParameterKey parameter;
  private static final Map<ParameterKey, DroitApplicatif> parameter2DroitMap = new HashMap<>(7);

  static {
    for (DroitApplicatif droit : DroitApplicatif.values()) {
      droit.parameter = ParameterKey.valueOf(droit.name());
      DroitApplicatif.parameter2DroitMap.put(droit.parameter, droit);
    }
  }

  public static DroitApplicatif valueOf(final ParameterKey param) {
    return DroitApplicatif.parameter2DroitMap.get(param);
  }

  @NonNull
  public ParameterKey getParameter() {
    return parameter;
  }

  public void setParameter(@NonNull final ParameterKey parameter) {
    if (parameter.equals(this.parameter)) return;

    DroitApplicatif.parameter2DroitMap.put(parameter, this);
    DroitApplicatif.parameter2DroitMap.remove(this.parameter);
    this.parameter = parameter;
  }

  @Override
  public String toString() {
    return getParameter().toString();
  }

}
