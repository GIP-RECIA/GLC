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
package fr.recia.glc.db.entities.personne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import fr.recia.glc.db.utils.IntConst;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Table de jointure entre personne et service avec la source et une date de fin.
 * <DL>
 * <DT><b>Champs obligatoires :</b></DT>
 * <DD>personne, service, source</DD>
 * </DL>
 *
 * @author GIP RECIA - Cailbourdin Nathan Février 2024
 */
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "apersonnes_services")
@AssociationOverrides({
        @AssociationOverride(name = "pk.personne",
                joinColumns = @JoinColumn(name = "APERSONNE_ID")),
        @AssociationOverride(name = "pk.service",
                joinColumns = @JoinColumn(name = "SERVICE_ID")) })
public class MappingAPersonneService implements Serializable {

    @EmbeddedId
    private MappingAPersonneServiceId pk = new MappingAPersonneServiceId();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    @Basic
    @Column(length = IntConst.ISOURCE, name = "SOURCE", nullable = false)
    private String source;

}
