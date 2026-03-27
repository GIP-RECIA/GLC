package fr.recia.glc.db.entities.gestion;

import fr.recia.glc.db.entities.common.AbstractSimpleEntity;
import fr.recia.glc.db.entities.groupe.RoleApplicatif;
import fr.recia.glc.db.entities.structure.AStructure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DroitsCategorie extends AbstractSimpleEntity {

	/** Droit de changement du password pour la catégorie d'utilisateur. False par défaut. */
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean changerPassword;
	/** Droit de changement de nom de login pour la catégorie d'utilisateur. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean changerLogin;
	/** Droit de changement d'identité pour la catégorie d'utilisateur. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean changerIdentite;
	/** Droit de modification de la fiche de données pour la catégorie d'utilisateur. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean modifierFiche;
	/** Droit de visibilité de la fiche de données pour la catégorie d'utilisateur. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean voirFiche;
	/** Modèle de login défini par l'établissement. */
	private String modeleLogin = "prenom.nom";

	/** Relation unidirectionnelle.
	 * Structure définissant ces droits. */
	@ManyToOne
	@JoinColumn(name = "structure_fk")
	private AStructure structure;

	/** Relation unidirectionnelle.
	 * RoleApplicatif pour lequel ces droits sont applicables.*/
	@ManyToOne
	@JoinColumn(name = "role_applicatif_fk")
	private RoleApplicatif roleApplicatif;

	/**
	 * Constructeur de l'objet DroitsCategorie.java.
	 * @param structure Structure définissant ces droits.
	 * @param roleApplicatif RoleApplicatif pour lequel ces droits sont applicables.
	 */
	public DroitsCategorie(final AStructure structure, final RoleApplicatif roleApplicatif) {
		super();
		this.structure = structure;
		this.roleApplicatif = roleApplicatif;
	}

	@Override
	public String toString() {
        return "DroitCategorie [" + super.toString() + ", " + this.changerPassword + ", " + this.changerLogin + ", " + this.changerIdentite +
            ", " + this.modifierFiche + ", " + this.voirFiche + ", " + this.modeleLogin + ", " + this.roleApplicatif + ", " + this.structure + "]";
	}

}
