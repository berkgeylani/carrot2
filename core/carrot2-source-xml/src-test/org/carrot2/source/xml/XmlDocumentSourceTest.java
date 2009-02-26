
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.xml;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.carrot2.core.IController;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Test cases for {@link XmlDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class XmlDocumentSourceTest extends DocumentSourceTestBase<XmlDocumentSource>
{
    private ResourceUtils resourceUtils = ResourceUtilsFactory.getDefaultResourceUtils();

    @Override
    public Class<XmlDocumentSource> getComponentClass()
    {
        return XmlDocumentSource.class;
    }

    @Test
    public void testLegacyXml()
    {
        IResource xml = resourceUtils.getFirst("/xml/carrot2-apple-computer.xml",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "results"),
            300);
        final int documentCount = runQuery();
        assertEquals(200, documentCount);
        assertEquals("apple computer", processingAttributes.get(AttributeNames.QUERY));
    }

    @Test
    public void testResultsTruncation()
    {
        IResource xml = resourceUtils.getFirst("/xml/carrot2-apple-computer.xml",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeNames.RESULTS, 50);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "readAll"), 
            false);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals("apple computer", processingAttributes.get(AttributeNames.QUERY));
    }

    @Test
    public void testXsltNoParameters()
    {
        IResource xml = resourceUtils.getFirst("/xml/custom-parameters-not-required.xml",
            XmlDocumentSourceTest.class);
        IResource xslt = resourceUtils.getFirst("/xsl/custom-xslt.xsl",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            xslt);
        final int documentCount = runQuery();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testXsltWithParameters()
    {
        IResource xml = resourceUtils.getFirst("/xml/custom-parameters-required.xml",
            XmlDocumentSourceTest.class);
        IResource xslt = resourceUtils.getFirst("/xsl/custom-xslt.xsl",
            XmlDocumentSourceTest.class);

        Map<String, String> xsltParameters = Maps.newHashMap();
        xsltParameters.put("id-field", "number");
        xsltParameters.put("title-field", "heading");
        xsltParameters.put("snippet-field", "snippet");
        xsltParameters.put("url-field", "url");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            xslt);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
            "xsltParameters"), xsltParameters);
        final int documentCount = runQuery();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testNoIdsInSourceXml()
    {
        IResource xml = resourceUtils.getFirst("/xml/carrot2-no-ids.xml",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        final int documentCount = runQuery();
        assertEquals(2, documentCount);
        assertEquals(Lists.newArrayList(0, 1), Lists.transform(getDocuments(),
            DOCUMENT_TO_ID));
    }

    @Test
    public void testGappedIdsInSourceXml()
    {
        IResource xml = resourceUtils.getFirst("/xml/carrot2-gapped-ids.xml",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        final int documentCount = runQuery();
        assertEquals(4, documentCount);
        assertEquals(Lists.newArrayList(6, 2, 5, 7), Lists.transform(getDocuments(),
            DOCUMENT_TO_ID));
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicatedIdsInSourceXml()
    {
        IResource xml = resourceUtils.getFirst("/xml/carrot2-duplicated-ids.xml",
            XmlDocumentSourceTest.class);

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        runQuery();
    }

    @Test
    public void testInitializationTimeXslt()
    {
        IResource xslt = resourceUtils.getFirst("/xsl/custom-xslt.xsl",
            XmlDocumentSourceTest.class);
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"), xslt);

        IResource xml = resourceUtils.getFirst("/xml/custom-parameters-not-required.xml",
            XmlDocumentSourceTest.class);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);

        final int documentCount = runQueryInCachingController();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testOverridingInitializationTimeXslt()
    {
        IResource initXslt = resourceUtils.getFirst("/xsl/carrot2-identity.xsl",
            XmlDocumentSourceTest.class);
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            initXslt);
        
        IController controller = getCachingController(initAttributes);

        // Run with identity XSLT
        {
            IResource xml = resourceUtils.getFirst("/xml/carrot2-test.xml",
                XmlDocumentSourceTest.class);
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }

        // Run with swapping XSLT
        {
            IResource xml = resourceUtils.getFirst("/xml/carrot2-test.xml",
                XmlDocumentSourceTest.class);
            IResource xslt = resourceUtils.getFirst(
                "/xsl/carrot2-title-snippet-switch.xsl", XmlDocumentSourceTest.class);
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);
            processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
                "xslt"), xslt);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }
    }

    @Test
    public void testDisablingInitializationTimeXslt()
    {
        IResource initXslt = resourceUtils.getFirst(
            "/xsl/carrot2-title-snippet-switch.xsl", XmlDocumentSourceTest.class);
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            initXslt);

        IController controller = getCachingController(initAttributes);
        
        // Run with swapping XSLT
        {
            IResource xml = resourceUtils.getFirst("/xml/carrot2-test.xml",
                XmlDocumentSourceTest.class);
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }

        // Run without XSLT
        {
            IResource xml = resourceUtils.getFirst("/xml/carrot2-test.xml",
                XmlDocumentSourceTest.class);
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);
            processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
                "xslt"), null);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }
    }

    @Test
    @Prerequisite(requires = "carrot2XmlFeedTestsEnabled")
    public void testRemoteUrl() throws MalformedURLException
    {
        IResource xml = new URLResourceWithParams(new URL(getCarrot2XmlFeedUrlBase()
            + "&q=${query}&results=${results}"));
        final String query = "apple computer";

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeNames.QUERY, query);
        processingAttributes.put(AttributeNames.RESULTS, 50);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals(query, processingAttributes.get(AttributeNames.QUERY));
    }

    private void assertTransformedDocumentsEqual(final int documentCount)
    {
        assertEquals(2, documentCount);
        assertEquals("xslt test", processingAttributes.get(AttributeNames.QUERY));
        assertEquals(Lists.newArrayList(498967, 831478), Lists.transform(getDocuments(),
            DOCUMENT_TO_ID));
        assertEquals(Lists.newArrayList("IBM's MARS Block Cipher.",
            "IBM WebSphere Studio Device Developer"), Lists.transform(getDocuments(),
            DOCUMENT_TO_TITLE));
        assertEquals(Lists.newArrayList(
            "The company's AES proposal using 128 bit blocks.",
            "An integrated development environment."), Lists.transform(getDocuments(),
            DOCUMENT_TO_SUMMARY));
        assertEquals(Lists.newArrayList("http://www.research.ibm.com/security/mars.html",
            "http://www-3.ibm.com/software/wireless/wsdd/"), Lists.transform(
            getDocuments(), DOCUMENT_TO_CONTENT_URL));
    }
}
