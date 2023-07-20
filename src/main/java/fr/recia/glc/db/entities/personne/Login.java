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
package fr.recia.glc.db.entities.personne;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"APERSONNE_LOGIN"}),
  @UniqueConstraint(columnNames = {"APERSONNE_ALIAS"})
})
@Getter
@Setter
public class Login extends AbstractEntity {

  /**
   * Nom de login.
   */
  @Column(unique = true, nullable = false, length = IntConst.I100)
  private String nom;

  /**
   * Relation bidirectionnelle.
   * Personne ayant ce login.
   */
  @OneToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "apersonne_login")
  private APersonne apersonneLogin;
  /**
   * Relation bidirectionnelle.
   * Personne ayant cet alias.
   */
  @OneToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "apersonne_alias")
  private APersonne apersonneAlias;
  /**
   * Relation bidirectionnelle.
   * Personne ayant eu ce login/alias.
   */
  @ManyToOne(cascade = {CascadeType.REFRESH})
  @JoinColumn(name = "apersonne_old_alias")
  private APersonne apersonneOldAlias;

  /**
   * Constructeur de l'objet Login.java.
   */
  public Login() {
    super();
  }

  /**
   * Constructeur de l'objet Login.java.
   *
   * @param nom Nom de login.
   */
  public Login(final String nom) {
    super();
    this.nom = nom;
  }

  /**
   * Transforme cette instance en chaine de caractères.
   *
   * @return <code>String</code> La chaine.
   * @see fr.recia.glc.db.entities.common.AbstractEntity#toString()
   */
  @Override
  public String toString() {
    return "Login [" +
      super.toString() + ", " +
      this.nom +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.nom == null) {
      result = prime * result;
    } else {
      result = prime * result + this.nom.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Login)) {
      return false;
    }
    final Login other = (Login) obj;
    if (this.nom == null) {
      if (other.nom != null) {
        return false;
      }
    } else if (!this.nom.equals(other.nom)) {
      return false;
    }
    return true;
  }

}
