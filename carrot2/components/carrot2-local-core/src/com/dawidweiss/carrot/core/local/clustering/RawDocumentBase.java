/*
 * Carrot2 Project Copyright (C) 2002-2004, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved. Refer
 * to the full license file "carrot2.LICENSE" in the root folder of the CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE Sponsored
 * by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local.clustering;

import com.stachoodev.util.common.*;

/**
 * An abstract implementation of some of the basic methods of the {@link
 * RawDocument} interface.
 * 
 * @author Dawid Weiss
 * @author stachoo
 * @version $Revision$
 */
public abstract class RawDocumentBase implements RawDocument, PropertyProvider
{
    /** Stores this document's properties */
    protected PropertyHelper propertyHelper;

    /**
     * Initializes the internal property storage.
     */
    public RawDocumentBase()
    {
        propertyHelper = new PropertyHelper();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getSnippet()
     */
    public String getSnippet()
    {
        return (String) propertyHelper.getProperty(PROPERTY_SNIPPET);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) propertyHelper.getProperty(PROPERTY_URL);
    }

    /* (non-Javadoc)
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getProperty(java.lang.String)
     */
    public Object getProperty(String name)
    {
        return propertyHelper.getProperty(name);
    }

    /* (non-Javadoc)
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setProperty(java.lang.String, java.lang.Object)
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getDoubleProperty(java.lang.String)
     */
    public double getDoubleProperty(String propertyName)
    {
        return propertyHelper.getDoubleProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propertyName)
    {
        return propertyHelper.getIntProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /**
     * @return Returns 'no score' constant.
     */
    public float getScore()
    {
        return -1;
    }
}