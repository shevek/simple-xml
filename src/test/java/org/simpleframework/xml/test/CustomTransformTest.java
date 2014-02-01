package org.simpleframework.xml.test;

import java.io.StringWriter;
import java.util.Arrays;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.ValidationTestCase;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.transform.RegistryMatcher;

/**
 *
 * @author shevek
 */
public class CustomTransformTest extends ValidationTestCase {

    public static class Property {

        public String text;

        public Property(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "P(" + text + ")";
        }

    }

    public static class PropertyConverter implements Converter<Property> {

        @Override
        public Property read(InputNode node) throws Exception {
            System.out.println("InputNode is " + node);
            InputNode anode = node.getAttribute("pvalue");
            System.out.println("AttributeNode is " + anode);
            assertNotNull("We got given something which is not a Property node", anode);
            return new Property(anode.getValue());
        }

        @Override
        public void write(OutputNode node, Property value) throws Exception {
            node.setAttribute("pvalue", value.text);
        }
    }

    @Root
    public static class Bean {

        @Element
        Object element;
        @ElementArray
        Object[] elementArray;

        @Override
        public String toString() {
            return "element=" + element + ", elementArray=" + Arrays.toString(elementArray);
        }
    }

    private void test(Persister persister) throws Exception {
        Bean bean = new Bean();
        bean.element = new Property("bar");
        bean.elementArray = new Property[]{new Property("baz"), new Property("qux")};
        StringWriter buffer = new StringWriter();
        persister.write(bean, buffer);
        System.out.println("Buffer is " + buffer);

        Bean copy = persister.read(Bean.class, buffer.toString());
        System.out.println("Copy is " + copy);
    }

    public void testWithCycleStrategy() throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Registry registry = new Registry();
        registry.bind(Property.class, new PropertyConverter());
        strategy = new RegistryStrategy(registry, strategy);
        Persister persister = new Persister(strategy);
        test(persister);
    }

    public void testWithRegistryStrategy() throws Exception {
        Registry registry = new Registry();
        registry.bind(Property.class, new PropertyConverter());
        Strategy strategy = new RegistryStrategy(registry);
        Persister persister = new Persister(strategy);
        test(persister);
    }
}
