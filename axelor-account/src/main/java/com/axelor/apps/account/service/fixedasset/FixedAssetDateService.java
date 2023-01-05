/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2023 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.service.fixedasset;

import com.axelor.apps.account.db.FixedAsset;
import java.time.LocalDate;

public interface FixedAssetDateService {

  /** Compute first depreciation date of economic, fiscal and ifrs plan */
  void computeFirstDepreciationDate(FixedAsset fixedAsset);

  /** Compute first depreciation date of fiscal */
  void computeFiscalFirstDepreciationDate(FixedAsset fixedAsset);

  /** Compute first depreciation date of economic */
  void computeEconomicFirstDepreciationDate(FixedAsset fixedAsset);

  /** Compute first depreciation date of ifrs plan */
  void computeIfrsFirstDepreciationDate(FixedAsset fixedAsset);

  /**
   * Compute and return the last day of the month/year depending on the periodicity type.
   *
   * @param fixedAsset
   * @param date
   */
  LocalDate computeLastDayOfPeriodicity(Integer periodicityType, LocalDate date);
}
