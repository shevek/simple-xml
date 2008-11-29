package org.simpleframework.xml.load;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.ValidationTestCase;

public class MixTest extends ValidationTestCase {
   
   private static final String SOURCE =
      /*
       
       <person-profile first-name='Niall' last-name='Gallagher'>
          <person-DOB>10/10/2008</person-DOB>
          <person-address>
             <house-number>10</house-number>
             <street-name>Sesame Street</street-name>
             <city>Disney Land</city>
          </person-address>
       </person-profile>
       */
   
   @Root
   public static class PersonProfile {

      @Attribute
      private String firstName;

      @Attribute
      private String lastName;

      @Element
      private PersonAddress personAddress;

      @Element
      private Date personDOB;

      public Date getDateOfBirth() {
         return personDOB;
      }

      public String getFirstName() {
         return firstName;
      }

      public String getLastName() {
         return lastName;
      }

      public PersonAddress getAddress() {
         return personAddress;
      }
   }

   @Root
   public static class PersonAddress {

      @Element
      private String houseNumber;

      @Element
      private String streetName;

      @Element
      private String city;

      public String getHouseNumber() {
         return houseNumber;
      }

      public String getStreetName() {
         return streetName;
      }

      public String getCity() {
         return city;
      }
   }

   
   @Root
   private static class MixExample {
      
      @ElementList
      private List<Object> list;
      
      @ElementMap
      private Map<Object, Object> map;
      
      @Element
      private Calendar calendar;
      
      public MixExample() {
         this.list = new ArrayList();
         this.map = new HashMap();
      }
      
      private void setTime(Date date) {
         calendar = new GregorianCalendar();
         calendar.setTime(date);
      }
      
      public void put(Object key, Object value) {
         map.put(key, value);
      }
      
      public Object get(int index) {
         return list.get(index);
      }
      
      public void add(Object object) {
         list.add(object);
      }
   }
   
   @Root
   private static class Entry {
      
      @Attribute
      private String id;
      
      @Text
      private String text;
      
      public Entry() {
         super();
      }
      
      public Entry(String id, String text) {
         this.id = id;
         this.text = text;
      }
   }
   
   public void testMix() throws Exception {
      Serializer serializer = new Persister();
      MixExample example = new MixExample();
      StringWriter source = new StringWriter();
      
      example.setTime(new Date());
      example.add("text");
      example.add(1);
      example.add(true);
      example.add(new Entry("1", "example 1"));
      example.add(new Entry("2", "example 2"));
      example.put(new Entry("1", "key 1"), new Entry("1", "value 1"));
      example.put("key 2", "value 2");
      example.put("key 3", 3);
      example.put("key 4", new Entry("4", "value 4"));
      
      serializer.write(example, System.out);
      serializer.write(example, source);   
      serializer.validate(MixExample.class, source.toString());
      
      MixExample other = serializer.read(MixExample.class, source.toString());
      
      serializer.write(other, System.out);
      
      assertEquals(example.get(0), "text");
      assertEquals(example.get(1), 1);      
      assertEquals(example.get(2), true);
      
      validate(example, serializer);
   }

}
