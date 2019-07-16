package com.ka09solutions.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

/**
 * Created by test on 24-01-2016.
 */
public class VelocityTemplateProcessor
{
    private static VelocityEngine velocityEngine;
    private static final String templateFolder = "/templates/";

    public VelocityTemplateProcessor()
    {
        if (velocityEngine == null)
        {
            velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.init();
        }
    }

    public String process(String templateName, Object data)
    {
        try
        {
            VelocityContext context = new VelocityContext();
            context.put("vdata", data);
            Template t = velocityEngine.getTemplate(templateFolder + templateName);
            StringWriter writer = new StringWriter();
            t.merge( context, writer );
            return(writer.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Error in preparing content";
        }
    }

}
