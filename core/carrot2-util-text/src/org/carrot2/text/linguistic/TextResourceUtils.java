package org.carrot2.text.linguistic;

import java.io.*;
import java.util.Set;

import org.carrot2.util.resource.Resource;

import com.google.common.collect.Sets;

/**
 * Utility class for loading language resources (such as common words). Resource files
 * must contain one word per line and be encoded in UTF-8, empty lines and lines starting
 * with <code>#</code> are ignored.
 */
final class TextResourceUtils
{
    /**
     * Loads words from a given {@link Resource} (one word per line).
     */
    public static Set<String> load(Resource wordsResource) throws IOException
    {
        final InputStream is = wordsResource.open();
        if (is == null)
        {
            throw new IOException("Resource stream handle is null: " + wordsResource);
        }

        final Set<String> words = Sets.newHashSet();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is,
            "UTF-8"));
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.startsWith("#") || line.length() == 0)
                {
                    continue;
                }

                words.add(line);
            }
        }
        finally
        {
            reader.close();
        }

        return words;
    }
}
