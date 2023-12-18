package com.choi.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @author 최의신
 *
 */
public class VelocityGenerator
{
    private VelocityContext context = null;

    public VelocityGenerator()
    {
        init();
    }

    /**
     *
     */
    private void init()
    {
        try
        {
			Properties prop = new Properties();
			
			prop.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
			prop.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            Velocity.init(prop);
            context = new VelocityContext();
        }catch(Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public java.lang.Object put(java.lang.String key, java.lang.Object value)
    {
        if ( context == null ) return null;
        return context.put(key, value);
    }
    
    public void clear() {
    	String [] ks = context.getKeys();
    	for(String k : ks)
    		context.remove(k);
    }

    /**
     * @param templateFile
     * @return
     */
    public String generate(String templateFile)
    {
        Template template =  null;
        ByteArrayOutputStream bo = null;

        try
        {
//        	 VelocityEngine ve = new VelocityEngine();
//        	 ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
//        	 ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//        	 template = ve.getTemplate(templateFile);
        	
            template = Velocity.getTemplate("vm/" + templateFile, "UTF-8");

            bo = new ByteArrayOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bo));

            if ( template != null)
                template.merge(context, writer);

            writer.flush();
            writer.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }

        return bo.toString();
    }
}
