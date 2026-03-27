package fr.recia.glc.db.entities.common;

import fr.recia.glc.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MailAlternate {

	/** Mail alternatif. */
	@Column(nullable = false)
	private String mail;
	/** Source de l'alimentation. */
	@Column(nullable = false, length = IntConst.ISOURCE)
	private String source;
	/** si utilisé. */
	@Column(nullable = false, columnDefinition = "BIT not null DEFAULT true")
	private boolean used;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailAlternate other = (MailAlternate) obj;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		return true;
	}


}
