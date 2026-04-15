// Carga un XML desde un archivo externo
async function loadXML(path) {
  const response = await fetch(path);
  const text = await response.text();
  return new DOMParser().parseFromString(text, "application/xml");
}

// Carga un XSL desde un archivo externo
async function loadXSL(path) {
  const response = await fetch(path);
  const text = await response.text();
  return new DOMParser().parseFromString(text, "application/xml");
}

// Aplica una transformación XSLT a un XML
async function transformXML(xmlPath, xslPath, targetElementId, params = {}) {
  const xml = await loadXML(xmlPath);
  const xsl = await loadXSL(xslPath);

  const processor = new XSLTProcessor();
  processor.importStylesheet(xsl);

  // Parámetros opcionales
  for (const [key, value] of Object.entries(params)) {
    processor.setParameter(null, key, value);
  }

  const result = processor.transformToFragment(xml, document);
  const target = document.getElementById(targetElementId);
  target.innerHTML = "";
  target.appendChild(result);
}
