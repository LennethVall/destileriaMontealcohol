<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>

  <xsl:template match="/">
    <table class="xml-table">
      <thead>
        <tr>
          <th>Imagen</th>
          <th>Código</th>
          <th>Nombre</th>
          <th>Precio</th>
          <th>Stock</th>
          <th>Tipo</th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="destileria/productos/producto">
          <tr>

            <!-- Imagen generada automáticamente -->
            <td>
              <img src="{concat('img/', @cod, '.jpg')}" 
                   alt="{@cod}" 
                   style="width:60px; height:auto; border-radius:6px;"/>
            </td>

            <td><xsl:value-of select="@cod"/></td>
            <td><xsl:value-of select="nombre"/></td>
            <td>€<xsl:value-of select="precio"/></td>
            <td><xsl:value-of select="stock"/></td>

            <td>
              <xsl:choose>
                <xsl:when test="tipo_bebida/Fermentadas">Fermentadas</xsl:when>
                <xsl:when test="tipo_bebida/Destiladas">Destiladas</xsl:when>
                <xsl:when test="tipo_bebida/Encabezadas">Encabezadas</xsl:when>
                <xsl:when test="tipo_bebida/Licores">Licores</xsl:when>
                <xsl:when test="tipo_bebida/Nuestra_Seleccion">Nuestra Selección</xsl:when>
              </xsl:choose>
            </td>

          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

</xsl:stylesheet>

