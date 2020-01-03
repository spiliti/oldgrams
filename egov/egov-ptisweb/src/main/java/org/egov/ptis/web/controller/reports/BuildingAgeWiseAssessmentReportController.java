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

package org.egov.ptis.web.controller.reports;

import static org.egov.infra.utils.JsonUtils.toJSON;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.egov.ptis.domain.dao.property.PropertyTypeMasterDAO;
import org.egov.ptis.domain.entity.property.PropertyTypeMaster;
import org.egov.ptis.domain.service.report.ReportService;
import org.egov.ptis.report.bean.BuidingAgeWiseReportHelperAdaptor;
import org.egov.ptis.report.bean.BuidingAgeWiseReportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/report/agewiseassessmentreport")
public class BuildingAgeWiseAssessmentReportController {

    @Autowired
    private PropertyTypeMasterDAO propertyTypeMasterDAO;

    @Autowired
    private ReportService reportService;

    @ModelAttribute("ownershiptype")
    public Map<Long, String> propertyType() {
        return getFormattedPropertyTypeMap(propertyTypeMasterDAO.findAll());
    }

    @ModelAttribute("buildingagefrom")
    public List<Integer> buildingAgeFrom() {
        return IntStream.rangeClosed(0, 299)
                .boxed().collect(Collectors.toList());

    }
    
    @ModelAttribute("buildingageto")
    public List<Integer> buildingAgeTo() {
        return IntStream.rangeClosed(0, 299)
                .boxed().collect(Collectors.toList());

    }

    @RequestMapping(method = RequestMethod.GET)
    public String searchAgeWiseForm(final Model model) {
        model.addAttribute("BuildingAgeWiseReport", new BuidingAgeWiseReportResult());
        model.addAttribute("mode", "buildingagewise");
        return "buildingagewise-form";
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void dcbReportSearchResult(@RequestParam final String boundaryId,
            @RequestParam final String mode, @RequestParam final String apartmentId,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final List<BuidingAgeWiseReportResult> resultList = null ; /*reportService
                .prepareQueryForBuidingAgeWiseReport(Long.valueOf(boundaryId), mode, Long.valueOf(apartmentId));*/
        final String result = new StringBuilder("{ \"data\":").append(toJSON(resultList, BuidingAgeWiseReportResult.class,
                BuidingAgeWiseReportHelperAdaptor.class)).append("}").toString();
        IOUtils.write(result, response.getWriter());
    }
    
    private Map<Long, String> getFormattedPropertyTypeMap(List<PropertyTypeMaster> propertyTypeList) {
        Map<Long, String> propertyTypeMap = new TreeMap<>();
        for (PropertyTypeMaster propertyTypeMaster : propertyTypeList) {
                propertyTypeMap.put(propertyTypeMaster.getId(),
                        propertyTypeMaster.getType());
        }
        return propertyTypeMap;
    }

}
