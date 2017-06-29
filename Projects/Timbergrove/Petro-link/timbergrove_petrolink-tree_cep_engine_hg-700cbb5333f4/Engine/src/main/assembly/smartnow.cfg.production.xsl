<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:conf="http://www.petrolink.com/mbe/config">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
	
	<!-- Identity transform-->
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

	<!-- Set RequestActiveFlows to true -->
    <xsl:template match="/conf:PetrolinkMBE/conf:InstanceGroups/conf:InstanceGroup/conf:Services/conf:MBEFlowsManagementService/conf:RequestActiveFlows/text()">true</xsl:template>

</xsl:stylesheet>