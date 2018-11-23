/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.model.payment;

import org.egov.commons.Bankaccount;
import org.egov.commons.CVoucherHeader;
import org.egov.eis.entity.DrawingOfficer;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.pims.commons.Position;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class Paymentheader extends StateAware<Position> {

    private static final long serialVersionUID = 1300661952219397466L;
    private Long id;
    @NotNull
    private CVoucherHeader voucherheader;
    @SafeHtml
    private String isSelected;
    @NotNull
    @SafeHtml
    @Length(max = 50)
    private String type;
    private Date concurrenceDate;

    private Bankaccount bankaccount;
    private BigDecimal paymentAmount;
    private DrawingOfficer drawingOfficer;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public CVoucherHeader getVoucherheader() {
        return voucherheader;
    }

    public void setVoucherheader(final CVoucherHeader voucherheader) {
        this.voucherheader = voucherheader;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Bankaccount getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(final Bankaccount bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(final String isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String getStateDetails() {
        // Added to show mode of paymentType for the inbox items.
        String temp = "";
        if (type != null && !type.isEmpty())
            temp = "-" + type.toUpperCase();
        return voucherheader.getVoucherNumber() + temp;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(final BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Override
    public String toString() {
        return type + " " + isSelected;
    }

    public Date getConcurrenceDate() {
        return concurrenceDate;
    }

    public void setConcurrenceDate(final Date concurrenceDate) {
        this.concurrenceDate = concurrenceDate;
    }

    public DrawingOfficer getDrawingOfficer() {
        return drawingOfficer;
    }

    public void setDrawingOfficer(final DrawingOfficer drawingOfficer) {
        this.drawingOfficer = drawingOfficer;
    }

}
