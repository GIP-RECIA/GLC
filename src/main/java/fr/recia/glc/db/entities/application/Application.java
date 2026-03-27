package fr.recia.glc.db.entities.application;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.entities.groupe.Profil;
import fr.recia.glc.db.entities.groupe.RoleApplicatif;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategorieApplication;
import fr.recia.glc.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Application extends AbstractEntity {

	//Attibuts
	/** Catégorie de l'application. */
	@Enumerated(EnumType.STRING)
	@Column(length = IntConst.I40)
	private CategorieApplication categorie;
	/** Description de l'application. */
	private String description;
	/** Identifiant unique de l'application. */
	@Column(unique = true, length = IntConst.I40)
	private String identifiant;
	/** Nom unique de l'application, pouvant servir de login. */
	@Column(unique = true, length = IntConst.I100)
	private String nom;
	/** Password de l'application. */
	private String password;

	//Relations
	/** Relation bidirectionnelle. */
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "applications_profils", joinColumns = @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID"))
	private Set<Profil> profils = new HashSet<>();

	/** Relation unidirectionnelle. */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "apersonne_fk")
	private APersonne proprietaire;

	/** Relation bidirectionnelle. */
	@OneToMany(mappedBy = "proprietaire", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	private Set<RoleApplicatif> rolesApplicatifs = new HashSet<>();

	@Override
	public String toString() {
        return "Application [" + super.toString() + ", " + this.categorie + ", " + this.identifiant + ", " + this.nom + ", " + this.description + ", " + this.password + "]";
	}

}
