package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("deprecation")
public class JettisonMappedContext extends JAXBContext
{
   private JAXBContext context;
   private MappedNamespaceConvention convention;

   public JettisonMappedContext(Class... classes)
   {
      this(new HashMap<String, String>(), new ArrayList<QName>(), new ArrayList<QName>(), classes);
   }

   public JettisonMappedContext(Mapped mapped, Class... classes)
   {
      List<QName> attributesAsElements = new ArrayList<QName>();
      for (String name : mapped.attributesAsElements())
      {
         System.out.println("XMLConstants.NULL_NS_URI: " + XMLConstants.NULL_NS_URI);
         System.out.println("XMLConstants.DEFAULT_NS_PREFIX: " + XMLConstants.DEFAULT_NS_PREFIX);
         QName qName = new QName(name);
         attributesAsElements.add(qName);
      }
      HashMap<String, String> xmlnsToJson = new HashMap<String, String>();
      for (XmlNsMap j : mapped.namespaceMap())
      {
         xmlnsToJson.put(j.namespace(), j.jsonName());
      }
      Configuration config = new Configuration(xmlnsToJson, attributesAsElements, new ArrayList());
      //convention = new MappedNamespaceConvention(config);
      convention = new MappedConvention(config);

      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public JettisonMappedContext(Map<String, String> xmlnsToJson, List<QName> attributesAsElements, List<QName> ignoredElements, Class... classes)
   {
      Configuration config = new Configuration(xmlnsToJson, attributesAsElements, ignoredElements);
      convention = new MappedNamespaceConvention(config);

      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public JettisonMappedContext(MappedNamespaceConvention convention, Class... classes)
   {
      this.convention = convention;
      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      return new JettisonMappedUnmarshaller(context, convention);
   }

   public Marshaller createMarshaller() throws JAXBException
   {
      return new JettisonMappedMarshaller(context, convention);
   }

   public Validator createValidator() throws JAXBException
   {
      return context.createValidator();
   }


}