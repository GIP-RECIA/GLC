package fr.recia.glc.db.entities.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractTimedEntity extends AbstractEntity implements Serializable {

	/** Donne l'information de la date de fin de l'objet lors de l'export. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateFin;

	@Override
	public String toString() {
        return "AbstractTimedEntity [" + super.toString() + ", " + this.dateFin + "]";
	}
}
