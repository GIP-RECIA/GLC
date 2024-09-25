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
import fr.recia.glc.configuration.bean.AlertProperties;
import fr.recia.glc.db.dto.AlertDto;
import fr.recia.glc.services.db.FonctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.recia.glc.services.alert.AlertStatics.SPLIT_CHARTER;

@Service
public class AlertService {

  @Autowired
  private FonctionService fonctionService;

  private final List<AlertProperties> alertProperties;

  public AlertService(GLCProperties glcProperties) {
    this.alertProperties = glcProperties.getAlerts();
  }

  private AlertDto buildMinFonctionAlert(AlertProperties.FonctionAlertProperties data, long nbDiscipline) {
    return buildFonctionAlert(FonctionAlertType.min, data.getCode(), data.getMin(), nbDiscipline);
  }

  private AlertDto buildMaxFonctionAlert(AlertProperties.FonctionAlertProperties data, long nbDiscipline) {
    return buildFonctionAlert(FonctionAlertType.max, data.getCode(), data.getMax(), nbDiscipline);
  }

  private AlertDto buildFonctionAlert(
    FonctionAlertType type, String code,
    AlertProperties.FonctionAlertProperties.ValueProperties value, long nbDiscipline
  ) {
    return AlertDto.builder()
      .title(type + SPLIT_CHARTER + code + SPLIT_CHARTER + value.getValue() + SPLIT_CHARTER + nbDiscipline)
      .type(value.getType())
      .action(value.isAction())
      .build();
  }

  public List<AlertDto> getFonctionAlerts(Long etablissementId, String etablissementSource) {
    List<AlertDto> alerts = new ArrayList<>();
    alertProperties.stream()
      .filter(alert -> Objects.equals(etablissementSource, alert.getSource()))
      .findAny().ifPresent(sourceAlerts -> sourceAlerts.getFonctionAlerts().forEach(fonctionAlert -> {
        String[] codes = fonctionAlert.getCode().split("-");
        long nbDiscipline = fonctionService.nbDiscipline(etablissementId, codes[0], codes[1]);
        if (fonctionAlert.getMin() != null && fonctionAlert.getMin().getValue() > 0
          && nbDiscipline < fonctionAlert.getMin().getValue())
          alerts.add(buildMinFonctionAlert(fonctionAlert, nbDiscipline));
        if (fonctionAlert.getMax() != null && fonctionAlert.getMax().getValue() > 0
          && nbDiscipline > fonctionAlert.getMax().getValue())
          alerts.add(buildMaxFonctionAlert(fonctionAlert, nbDiscipline));
      }));

    return alerts;
  }

}
