<%@ page contentType="text/json"%><%@ taglib prefix="s"
	uri="/WEB-INF/tags/struts-tags.tld"%><s:if test="accountDetailType==0">Please select detail type</s:if>
<s:else>
	<s:if test="entitiesList.size() == 0"> Nothing found to display
</s:if>
	<s:else>
		<s:iterator var="s" value="entitiesList" status="status">
			<s:property value="%{code}" />`-`<s:property value="%{name}" />`~`<s:property
				value="%{id}" />~^</s:iterator>
	</s:else>
</s:else>
