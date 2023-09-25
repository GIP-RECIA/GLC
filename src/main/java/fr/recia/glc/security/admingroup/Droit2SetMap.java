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

import lombok.NonNull;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class Droit2SetMap<T> extends EnumMap<DroitApplicatif, Set<T>> {

  public Droit2SetMap() {
    super(DroitApplicatif.class);
  }

  /**
   * Donne l'ensemble des objets de type T associée aux droits.
   * Le résultat peut être vide, mais pas null.
   *
   * @param droitApplicatif
   * @return Set<IStructure>
   */
  @NonNull
  public Set<T> getSet(@NonNull final DroitApplicatif droitApplicatif) {
    Set<T> set = get(droitApplicatif);
    if (set == null) {
      set = new HashSet<>();
      put(droitApplicatif, set);
    }

    return set;
  }

  /**
   * Ajout un object de type T à un droit.
   * Si l'objet n'était pas associé au droit et est ajouté, renvoie true ; false sinon.
   *
   * @param droitApplicatif
   * @param T               object
   * @return true si modification
   */
  public boolean add(@NonNull final DroitApplicatif droitApplicatif, final T objectT) {
    return getSet(droitApplicatif).add(objectT);
  }

  /**
   * @param droitApplicatif
   * @param structures
   * @return true si l'ensemble a été modifié.
   */
  public boolean addAll(@NonNull final DroitApplicatif droitApplicatif, final Collection<T> objectT) {
    return getSet(droitApplicatif).addAll(objectT);
  }

  /**
   * Test s'il existe object T pour le droit.
   *
   * @param droit
   * @return
   */
  public boolean test(@NonNull final DroitApplicatif droit) {
    return !getSet(droit).isEmpty();
  }

  /**
   * Test s'il existe object T pour un des droits.
   *
   * @param droits
   * @return
   */
  public boolean test(@NonNull final DroitApplicatif droit, @NonNull final DroitApplicatif... droits) {
    if (getSet(droit).isEmpty()) return test(droits);
    return true;
  }

  /**
   * Test s'il existe object T pour un des droits.
   *
   * @param droits
   * @return
   */
  public boolean test(@NonNull final DroitApplicatif[] droits) {
    for (DroitApplicatif droitApplicatif : droits) {
      if (!getSet(droitApplicatif).isEmpty()) return true;
    }
    return false;
  }

  public boolean test(@NonNull final T objectT, @NonNull final DroitApplicatif droit) {
    return getSet(droit).contains(objectT);
  }

  public boolean test(@NonNull final T objectT, @NonNull final DroitApplicatif droit, @NonNull final DroitApplicatif... droits) {
    if (getSet(droit).contains(objectT)) return true;
    return test(objectT, droits);
  }

  /**
   * Test si l'objet T est associé à un des droits.
   *
   * @param objectT
   * @param droits
   * @return
   */
  public boolean test(@NonNull final T objectT, @NonNull final DroitApplicatif[] droits) {
    for (DroitApplicatif droitApplicatif : droits) {
      if (getSet(droitApplicatif).contains(objectT)) return true;
    }
    return false;
  }

  /**
   * Test si un des object T de la collection est associé à un des droits.
   */
  public boolean test(@NonNull final Iterable<T> collection, @NonNull final DroitApplicatif... droits) {
    int cpt = droits.length;
    DroitApplicatif[] reste = new DroitApplicatif[cpt];

    cpt = 0;

    for (DroitApplicatif droit : droits) {
      Set<T> set = getSet(droit);
      if (!set.isEmpty()) {
        reste[cpt++] = droit;
      }
    }

    if (cpt == 0) return false;

    for (T t : collection) {
      if (t != null && test(t, reste)) return true;
    }

    return false;
  }

  /**
   * Test si un des object T du tableau est associé à un des droits.
   */
  public boolean test(@NonNull final T[] collection, @NonNull final DroitApplicatif... droits) {
    int cpt = droits.length;
    DroitApplicatif[] reste = new DroitApplicatif[cpt];

    cpt = 0;

    for (DroitApplicatif droit : droits) {
      Set<T> set = getSet(droit);
      if (!set.isEmpty()) {
        reste[cpt++] = droit;
      }
    }

    if (cpt == 0) return false;

    for (T t : collection) {
      if (test(t, reste)) return true;
    }

    return false;
  }

}
