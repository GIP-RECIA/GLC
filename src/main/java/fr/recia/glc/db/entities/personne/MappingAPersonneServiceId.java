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

import fr.recia.glc.db.entities.common.TypeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MappingAPersonneServiceId implements Serializable {

    /** La personne qui possède le service */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "APERSONNE_ID")
    private APersonne personne;

    /** Le service exercé par la personne */
    @ManyToOne
    @JoinColumn(name = "SERVICE_ID")
    private TypeService service;

    @Override
    public String toString() {
        return "MappingAPersonneService [apersonne=" + personne.getUid() + ", typeservice=" + service.getLibelleService() + "]";
    }

}
