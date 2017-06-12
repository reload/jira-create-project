package ut.dk.reload.atlassian.plugins.project_template;

import org.junit.Test;
import dk.reload.atlassian.plugins.project_template.api.MyPluginComponent;
import dk.reload.atlassian.plugins.project_template.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}