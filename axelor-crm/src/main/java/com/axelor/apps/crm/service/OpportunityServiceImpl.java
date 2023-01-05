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
package com.axelor.apps.crm.service;

import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.SequenceRepository;
import com.axelor.apps.base.service.AddressService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.crm.db.Lead;
import com.axelor.apps.crm.db.Opportunity;
import com.axelor.apps.crm.db.repo.OpportunityRepository;
import com.axelor.apps.crm.exception.CrmExceptionMessage;
import com.axelor.apps.message.db.EmailAddress;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class OpportunityServiceImpl implements OpportunityService {

  @Inject protected OpportunityRepository opportunityRepo;

  @Inject protected AddressService addressService;

  @Transactional
  public void saveOpportunity(Opportunity opportunity) {
    opportunityRepo.save(opportunity);
  }

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public Partner createClientFromLead(Opportunity opportunity) throws AxelorException {
    Lead lead = opportunity.getLead();
    if (lead == null) {
      throw new AxelorException(
          opportunity,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(CrmExceptionMessage.LEAD_PARTNER));
    }

    String name = lead.getFullName();

    Address address = null;
    if (lead.getPrimaryAddress() != null) {
      // avoids printing 'null'
      String addressL6 =
          lead.getPrimaryPostalCode() == null ? "" : lead.getPrimaryPostalCode() + " ";
      addressL6 += lead.getPrimaryCity() == null ? "" : lead.getPrimaryCity().getName();

      address =
          addressService.createAddress(
              null, null, lead.getPrimaryAddress(), null, addressL6, lead.getPrimaryCountry());
      address.setFullName(addressService.computeFullName(address));
    }

    EmailAddress email = null;
    if (lead.getEmailAddress() != null) {
      email = new EmailAddress(lead.getEmailAddress().getAddress());
    }

    Partner partner =
        Beans.get(PartnerService.class)
            .createPartner(
                name,
                null,
                lead.getFixedPhone(),
                lead.getMobilePhone(),
                email,
                opportunity.getCurrency(),
                address,
                address,
                true);

    opportunity.setPartner(partner);
    opportunityRepo.save(opportunity);

    return partner;
  }

  @Override
  public void setSequence(Opportunity opportunity) throws AxelorException {
    Company company = opportunity.getCompany();
    String seq =
        Beans.get(SequenceService.class).getSequenceNumber(SequenceRepository.OPPORTUNITY, company);
    if (seq == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(CrmExceptionMessage.OPPORTUNITY_1),
          company != null ? company.getName() : null);
    }
    opportunity.setOpportunitySeq(seq);
  }
}
