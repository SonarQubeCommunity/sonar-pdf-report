/*
 * Sonar PDF Report (Maven plugin)
 * Copyright (C) 2010 klicap - ingenieria del puzle
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.report.pdf.entity;

public class MeasuresTest {
  //
  // private static final String DEFAULT_VERSION_FOR_MEASURES = "N/A";
  // private static final String MEASURES_GROUP = "measures";
  // private static final String RESOURCES_ELEMENT = "resources";
  // private static final String VERSION_TO_TEST = "1.0";
  // private static final String DATE_TO_TEST = "2014-03-27T00:00:00";
  // private static final String RESOURCE_ELEMENT = "resource";
  // private static final String VERSION_ELEMENT = "version";
  // private static final String DATE_ELEMENT = "date";
  // private static final String MEASURE_ELEMENT = "msr";
  // private static final String KEY_ELEMENT = "key";
  //
  // /**
  // * This method simply creates an empty document
  // *
  // * @return
  // */
  // private Document createEmptyDocument() {
  // Document document = DocumentHelper.createDocument();
  // return document;
  // }
  //
  // /**
  // * This method adds //resources/resource to the document and returns the
  // * resource element.
  // *
  // * @param document
  // * @return
  // */
  // private Element addResourcesAndResourceElements(final Document document) {
  // return document.addElement(RESOURCES_ELEMENT).addElement(RESOURCE_ELEMENT);
  // }
  //
  // /**
  // * This method builds a small msr element with the given key. This appears
  // to
  // * be the minimal viable definition of a measure. This returns the resulting
  // * measure element.
  // *
  // * @param element
  // * - this should be the //resources/resource element
  // * @param key
  // * @return the measure element for reuse
  // */
  // private Element addMeasureToElementWithKey(final Element element, final
  // String key) {
  // Element measureElement = element.addElement(MEASURE_ELEMENT);
  // measureElement.addElement(KEY_ELEMENT).addText(key);
  // return measureElement;
  // }
  //
  // @Test(alwaysRun = true, enabled = true, groups = { MEASURES_GROUP })
  // public void testAddMeasureFromNode() {
  // Document document = createEmptyDocument();
  // Element resource = addResourcesAndResourceElements(document);
  // String myMeasureKey = "my-measure";
  // addMeasureToElementWithKey(resource, myMeasureKey);
  //
  // Measures measures = new Measures();
  // measures.addMeasureFromNode(document.selectSingleNode("//resources/resource/msr"));
  // assertEquals(1, measures.getMeasuresCount());
  // assertTrue(measures.containsMeasure(myMeasureKey));
  // assertEquals(myMeasureKey, measures.getMeasuresKeys().toArray()[0]);
  // assertEquals(myMeasureKey, measures.getMeasure(myMeasureKey).getKey());
  // }
  //
  // @Test(alwaysRun = true, enabled = true, groups = { MEASURES_GROUP })
  // public void testAddAllMeasuresFromDocument() {
  // Document document = createEmptyDocument();
  // Element resource = addResourcesAndResourceElements(document);
  // resource.addElement(DATE_ELEMENT).addText(DATE_TO_TEST);
  // resource.addElement(VERSION_ELEMENT).addText(VERSION_TO_TEST);
  // String someElementKey = "some-element";
  // addMeasureToElementWithKey(resource, someElementKey);
  //
  // Measures measures = new Measures();
  // measures.addAllMeasuresFromDocument(document);
  // assertEquals(DATE_TO_TEST, getFormattedDateString(measures.getDate()));
  // assertEquals(VERSION_TO_TEST, measures.getVersion());
  // assertEquals(1, measures.getMeasuresCount());
  // assertEquals(someElementKey, measures.getMeasuresKeys().toArray()[0]);
  // }
  //
  // @Test(alwaysRun = true, enabled = true, groups = { MEASURES_GROUP })
  // public void testAddAllMeasuresFromDocumentWithEmptyDocument() {
  // Document document = createEmptyDocument();
  //
  // Measures measures = new Measures();
  // measures.addAllMeasuresFromDocument(document);
  // assertNull(measures.getDate());
  // assertEquals(DEFAULT_VERSION_FOR_MEASURES, measures.getVersion());
  // assertEquals(0, measures.getMeasuresCount());
  //
  // }
  //
  // @Test(alwaysRun = true, enabled = true, groups = { MEASURES_GROUP })
  // public void testDateFromAddAllMeasuresFromDocument() {
  // Document document = createEmptyDocument();
  // addResourcesAndResourceElements(document).addElement(DATE_ELEMENT).addText(DATE_TO_TEST);
  //
  // Measures measures = new Measures();
  // measures.addAllMeasuresFromDocument(document);
  // String formattedMeasuresDate = getFormattedDateString(measures.getDate());
  // assertEquals(DATE_TO_TEST, formattedMeasuresDate);
  // }
  //
  // @Test(alwaysRun = true, enabled = true, groups = { MEASURES_GROUP })
  // public void testVersionFromAddAllMeasuresFromDocument() {
  // Document document = createEmptyDocument();
  // addResourcesAndResourceElements(document).addElement(VERSION_ELEMENT).addText(VERSION_TO_TEST);
  //
  // Measures measures = new Measures();
  // measures.addAllMeasuresFromDocument(document);
  // assertEquals(VERSION_TO_TEST, measures.getVersion());
  // }
  //
  // private String getFormattedDateString(final Date date) {
  // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  // return df.format(date);
  // }

}
