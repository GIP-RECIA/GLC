package fr.recia.glc.db.entities.groupe;

import fr.recia.glc.db.entities.application.Application;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Profil extends AGroupeOfAPersonne {

	/** Définition de la règle de peuplement. */
	private String reglePeuplement;

		//Relations
	/** Relation bidirectionnelle.
	 * Structure ayant défini le profil. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "structure_fk")
	private AStructure proprietaire;

	/** Relation bidirectionnelle.
	 * Listes des applications ayant besoin de ce profil.*/
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "profils")
	private Set<Application> applications = new HashSet<>();

	public Profil() {
		super();
		this.setCategorie(CategorieGroupe.Profil);
	}

	public Profil(final String cn, final Set<MappingAGroupeAPersonne> membres,
			final String reglePeuplement, final String source) {
		super(cn, CategorieGroupe.Profil, membres, source);
		this.reglePeuplement = reglePeuplement;
	}

	@Override
	public String toString() {
        return "Profil [" + super.toString() + ", " + this.reglePeuplement + "]";
	}

}
