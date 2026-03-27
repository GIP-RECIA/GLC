package fr.recia.glc.db.entities.common;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public abstract class AbstractSimpleEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Override
	public String toString() {
        return "AbstractSimpleEntity [" + this.id + "]";
	}

}
