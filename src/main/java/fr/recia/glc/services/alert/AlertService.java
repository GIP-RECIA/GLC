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

package fr.recia.glc.services.alert;

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.configuration.bean.CustomConfigProperties;
import fr.recia.glc.db.dto.AlertDto;
import fr.recia.glc.db.dto.AlertType;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.web.dto.function.DisciplineAlertDto;
import fr.recia.glc.web.dto.function.FiliereAlertDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AlertService {

    @Autowired
    private FonctionService fonctionService;

    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;

    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;

    private final List<CustomConfigProperties.AlertProperties> alertProperties;

    public AlertService(GLCProperties glcProperties) {
        this.alertProperties = glcProperties.getCustomConfig().getAlerts();
    }

    private AlertDto buildFonctionAlert(TypeFonctionFiliereDto filiere, DisciplineDto discipline, int min, int max, int nbActuel, AlertType level, FonctionAlertType type, boolean action) {
        return AlertDto.builder()
            .filiere(new FiliereAlertDto(filiere.getId(), filiere.getLibelle()))
            .discipline(new DisciplineAlertDto(discipline.getId(), discipline.getLibelle()))
            .min(min)
            .max(max)
            .nbActuel(nbActuel)
            .level(level)
            .type(type)
            .action(action)
            .build();
    }

    @Cacheable(value = "fonctionAlerts", key = "#etablissementId")
    public List<AlertDto> getFonctionAlerts(Long etablissementId, String etablissementSource) {
        log.trace("getFonctionAlerts for {} and {}", etablissementId, etablissementSource);
        List<AlertDto> alerts = new ArrayList<>();
        for (CustomConfigProperties.AlertProperties alert : alertProperties) {
            // On ne regarde que les alertes de la même source que celle de l'établissement
            if (etablissementSource.equals(alert.getSource())) {
                List<CustomConfigProperties.AlertProperties.FonctionAlertProperties> fonctionAlerts = alert.getFonctionAlerts();
                if (fonctionAlerts != null) {
                    // Pour chaque alerte de cette source on regarde si on est dans ce cas
                    for (CustomConfigProperties.AlertProperties.FonctionAlertProperties fonctionAlert : fonctionAlerts) {
                        long nbDiscipline = fonctionService.nbDiscipline(etablissementId, fonctionAlert.getFiliere(), fonctionAlert.getDiscipline());
                        if (fonctionAlert.getMin() != null && fonctionAlert.getMin().getValue() > 0 && nbDiscipline < fonctionAlert.getMin().getValue()) {
                            // TODO : cache + min/max à null
                            final TypeFonctionFiliereDto filiere = typeFonctionFiliereRepository.findByCodeAndSourceSarapis(fonctionAlert.getFiliere(), etablissementSource);
                            final DisciplineDto discipline = disciplineRepository.findByCodeAndSourceSarapis(fonctionAlert.getDiscipline(), etablissementSource);
                            alerts.add(buildFonctionAlert(filiere, discipline, fonctionAlert.getMin().getValue(),
                                fonctionAlert.getMax().getValue(), (int) nbDiscipline, fonctionAlert.getMin().getType(), FonctionAlertType.min, fonctionAlert.getMin().isAction()));
                        }
                        if (fonctionAlert.getMax() != null && fonctionAlert.getMax().getValue() > 0 && nbDiscipline > fonctionAlert.getMax().getValue()) {
                            final TypeFonctionFiliereDto filiere = typeFonctionFiliereRepository.findByCodeAndSourceSarapis(fonctionAlert.getFiliere(), etablissementSource);
                            final DisciplineDto discipline = disciplineRepository.findByCodeAndSourceSarapis(fonctionAlert.getDiscipline(), etablissementSource);
                            alerts.add(buildFonctionAlert(filiere, discipline, fonctionAlert.getMin().getValue(),
                                fonctionAlert.getMax().getValue(), (int) nbDiscipline, fonctionAlert.getMax().getType(), FonctionAlertType.max, fonctionAlert.getMax().isAction()));
                        }
                    }
                }
                break;
            }
        }
        return alerts;
    }

}
