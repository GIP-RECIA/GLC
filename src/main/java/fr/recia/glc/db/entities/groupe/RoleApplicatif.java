/**
 * Copyright (C) 2008-2014  GIP RECIA (Groupement d'Intérêt Public REgion
 * Centre InterActive)
 *
 * This file is part of Sarapis.
 *
 * Sarapis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sarapis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package fr.recia.glc.db.entities.groupe;

import fr.recia.glc.db.entities.application.Application;
import fr.recia.glc.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RoleApplicatif extends AGroupeOfAPersonne {

	/** Relation unidirectionnelle.
	 * Liste des profils utiles au rôle applicatif. */
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "roles_applicatifs_profils",
			joinColumns =
				@JoinColumn(name = "ROLEAPPLICATIF_ID", referencedColumnName = "ID"),
			inverseJoinColumns =
				@JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID"))
	private Set<Profil> profils = new HashSet<>();

	/** Relation bidirectionnelle.
	 * Application pour laquelle le role applicatif est défini.*/
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "APPLICATION_ID")
	private Application proprietaire;

	public RoleApplicatif() {
		super();
		this.setCategorie(CategorieGroupe.Role_applicatif);
	}

	public RoleApplicatif(final String cn, final Set<MappingAGroupeAPersonne> membres, final Application application, final String source) {
		super(cn, CategorieGroupe.Role_applicatif, membres, source);
		this.proprietaire = application;
	}

	@Override
	public String toString() {
        return "RoleApplicatif [" + super.toString() + ", " + this.profils + "]";
	}

}
