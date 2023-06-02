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
package fr.recia.glc.db.entities.education;

import fr.recia.glc.db.utils.IntConst;
import fr.recia.glc.db.entities.common.AbstractSimpleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;

/**
 * Bean - Entity.
 * Descriptipn d'une matière d'enseignement.
 * <DL><DT><b>Champs obligatoires :</b></DT>
 * <DD>code, matiere, source.</DD></DL>
 *
 * @author GIP RECIA - Gribonvald Julien
 * 9 juin 08
 */
@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code", "matiere", "source"})
})
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Enseignement extends AbstractSimpleEntity {

  /**
   * Identifiant de sérialisation.
   */
  private static final long serialVersionUID = -931036498287981852L;

  //Attributs
  /**
   * Code de la discipline.
   */
  @Column(/*nullable = false,*/length = IntConst.I20)
  private String code;
  /**
   * Année scolaire de validité de l'objet.
   * Année à la rentrée de septembre.
   */
  @Temporal(TemporalType.DATE)
  private Date anneeScolaire;
  /**
   * Libellé de la matière enseignée.
   */
  @Column(nullable = false, length = IntConst.I128)
  private String matiere;
  /**
   * Source d'alimentation de la discipline de poste.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;
  /**
   * Code matière de rattachement National, si matière académique.
   */
  @Column(length = IntConst.I20)
  private String codeRattach;

  //Constructeurs

  /**
   * Constructeur de l'objet Enseignement.java.
   */
  public Enseignement() {
    super();
  }

  /**
   * Constructeur de l'objet Enseignement.java.
   *
   * @param matiere Libellé de la matière enseignée.
   * @param source  Source d'alimentation de l'objet.
   */
  public Enseignement(final String matiere, final String source) {
    super();
    this.matiere = matiere;
    this.source = source;
  }

  /**
   * Constructeur de l'objet Enseignement.java.
   *
   * @param code    Code de la matière.
   * @param matiere Libellé de la matière enseignée.
   * @param source  Source d'alimentation de l'objet.
   */
  public Enseignement(final String code, final String matiere, final String source) {
    super();
    this.code = code;
    this.matiere = matiere;
    this.source = source;
  }

  //Accesseurs

  /**
   * Getter du membre code.
   *
   * @return <code>String</code> le membre code
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Setter du membre code.
   *
   * @param code la nouvelle valeur du membre code
   */
  public void setCode(final String code) {
    this.code = code;
  }

  /**
   * Getter of member anneeScolaire.
   *
   * @return <code>Date</code> the attribute anneeScolaire
   */
  public Date getAnneeScolaire() {
    return anneeScolaire;
  }

  /**
   * Setter of attribute anneeScolaire.
   *
   * @param anneeScolaire the attribute anneeScolaire to set
   */
  public void setAnneeScolaire(final Date anneeScolaire) {
    this.anneeScolaire = anneeScolaire;
  }

  /**
   * Getter du membre matiere.
   *
   * @return <code>String</code> le membre matiere
   */
  public String getMatiere() {
    return this.matiere;
  }

  /**
   * Setter du membre matiere.
   *
   * @param matiere la nouvelle valeur du membre matiere
   */
  public void setMatiere(final String matiere) {
    this.matiere = matiere;
  }

  /**
   * Getter of member source.
   *
   * @return <code>String</code> the attribute source
   */
  public String getSource() {
    return source;
  }

  /**
   * Setter of member source.
   *
   * @param source the source to set
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * Getter of member codeRattach.
   *
   * @return <code>String</code> le membre codeRattach
   */
  public String getCodeRattach() {
    return codeRattach;
  }

  /**
   * Setter of member codeRattach.
   *
   * @param codeRattach la nouvelle valeur du membre codeRattach
   */
  public void setCodeRattach(final String codeRattach) {
    this.codeRattach = codeRattach;
  }

  /**
   * Transforme cette instance en chaine de caractères.
   *
   * @return <code>String</code> La chaine.
   * @see fr.recia.glc.db.entities.common.AbstractEntity#toString()
   */
  @Override
  public String toString() {
    return "Enseignement [" +
      super.toString() + ", " +
      this.code + ", " +
      this.matiere + ", " +
      this.source + ", " +
      this.codeRattach + ", " +
      this.anneeScolaire +
      "]";
  }

  /**
   * Donne la valeur de hachage de l'instance.
   *
   * @return <code>int</code> La valeur du hash.
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.code == null) {
      result = prime * result;
    } else {
      result = prime * result + this.code.hashCode();
    }
		/* if (this.matiere == null) {
			result = prime * result;
		} else {
			result = prime * result + this.matiere.hashCode();
		}*/
    if (this.source == null) {
      result = prime * result;
    } else {
      result = prime * result + this.source.hashCode();
    }
    return result;
  }

  /**
   * Teste si un objet est égal à cette instance.
   *
   * @param obj l'instance le l'object à comparer.
   * @return <code>boolean</code> : vrai si l'instance est identique, faux sinon
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Enseignement)) {
      return false;
    }
    final Enseignement other = (Enseignement) obj;
    if (this.code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!this.code.equals(other.code)) {
      return false;
    }
		/* if (this.matiere == null) {
			if (other.matiere != null) {
				return false;
			}
		} else if (!this.matiere.equals(other.matiere)) {
			return false;
		}*/
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    return true;
  }

  /**
   * Teste si un objet est égal à cette instance.
   * @param obj l'instance le l'object à comparer.
   * @return <code>boolean</code> : vrai si l'instance est identique, faux sinon
   * @see java.lang.Object#equals(java.lang.Object)
   */
	/* public boolean equalsOnSourceCode(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Enseignement)) {
			return false;
		}
		final Enseignement other = (Enseignement) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}
		if (this.source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!this.source.equals(other.source)) {
			return false;
		}
		return true;
	}*/

}
