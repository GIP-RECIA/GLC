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
package fr.recia.glc.db.entities.structure;

/**
 * Type énuméré de l'état d'alimentation des structures.
 *
 * @author GIP RECIA - Gribonvald Julien
 * 28 mai 09
 */
public enum EtatAlim {
  /**
   * Non alimenté.
   */
  Non_alimente,
  /**
   * En bascule, attente du complet.
   */
  Bascule,
  /**
   * Traitement quotidient des deltas.
   */
  Ouvert
}
