import com.fasterxml.jackson.databind.JsonNode;
import controllers.CrudController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.NOT_FOUND;
import static play.test.Helpers.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

/*
 * Integration Tests for the Application routes. They are done using a "fake application" (FakeApplication class) that
 * provides a running Application as context
 */
public class CrudRoutesTest
{

  private final String serverUrl = "http://localhost:3333";
  private static JsonNode tElement1;
  private static JsonNode tElement2;

  /**
   * One-time initialization code.
   * (Called once before any of the test methods in the class).
   */
  @BeforeClass public static void oneTimeSetUp()
  {
  }

  /**
   * (Called once after all the test methods in the class).
   */
  @AfterClass public static void oneTimeTearDown()
  {
  }

  /**
   * Sets up the test fixture.
   * (Called before every test case method.)
   */
  @Before public void setUp()
  {
    tElement1 = Json.newObject().put("name", "element1 name").put("value", "element1 value");
    tElement2 = Json.newObject().put("name", "element2 name").put("value", "element2 value");
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        deleteAllTElements();
      }
    });
  }

  /**
   * Tears down the test fixture.
   * (Called after every test case method.)
   */
  @After public void tearDown()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        deleteAllTElements();
      }
    });
  }

  /**
   * TEST METHODS
   */

  @Test public void createTElementTest()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        // Invoke the "Create" action using the Router
        Result result = route(new FakeRequest(POST, "/telements").withJsonBody(tElement1));
        // Check response is OK
        Assert.assertEquals(OK, status(result));
        // Check Content-Type
        Assert.assertEquals("application/json", contentType(result));
        // Check Charset
        Assert.assertEquals("utf-8", charset(result));
        // Check fields
        JsonNode actual = Json.parse(contentAsString(result));
        String actualId = actual.get("_id").get("$oid").asText();
        JsonNode expected = tElement1;
        Assert.assertNotNull(actual.get("name"));
        Assert.assertEquals(expected.get("name"), actual.get("name"));
        Assert.assertNotNull(actual.get("value"));
        Assert.assertEquals(expected.get("value"), actual.get("value"));
      }
    });
  }

  @Test public void findAllTElementsTest()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        // Create two sample elements
        tElement1 = Json.parse(contentAsString(route(new FakeRequest(POST, "/telements").withJsonBody(tElement1))));
        tElement2 = Json.parse(contentAsString(route(new FakeRequest(POST, "/telements").withJsonBody(tElement2))));
        // Invoke the "Find All" action using the Router
        Result result = route(new FakeRequest(GET, "/telements"));
        // Check response is OK
        Assert.assertEquals(OK, status(result));
        // Check Content-Type
        Assert.assertEquals("application/json", contentType(result));
        // Check Charset
        Assert.assertEquals("utf-8", charset(result));
        // Store actual and expected results into two sets, to compare them
        Set expectedSet = new HashSet<JsonNode>();
        expectedSet.add(tElement1);
        expectedSet.add(tElement2);
        Set actualSet = new HashSet<JsonNode>();
        JsonNode jsonResponse = Json.parse((contentAsString(result)));
        Iterator it = jsonResponse.iterator();
        while (it.hasNext()) {
          actualSet.add(it.next());
        }
        // Check the number of results
        Assert.assertEquals(expectedSet.size(), actualSet.size());
        // Check the results
        Assert.assertEquals(expectedSet, actualSet);
      }
    });
  }

  @Test public void findTElementByIdTest()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        // Create an element
        JsonNode expected = Json
          .parse(contentAsString(route(new FakeRequest(POST, "/telements").withJsonBody(tElement1))));
        String id = expected.get("_id").get("$oid").asText();
        // Invoke the "Find by Id" action using the Router
        Result result = route(new FakeRequest(GET, "/telements/" + id));
        // Check response is OK
        Assert.assertEquals(OK, status(result));
        // Check Content-Type
        Assert.assertEquals("application/json", contentType(result));
        // Check Charset
        Assert.assertEquals("utf-8", charset(result));
        // Check the element retrieved
        JsonNode actual = Json.parse((contentAsString(result)));
        Assert.assertEquals(expected, actual);
      }
    });
  }

  @Test public void updateTElementTest()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        // Create an element
        JsonNode elementCreated = Json
          .parse(contentAsString(route(new FakeRequest(POST, "/telements").withJsonBody(tElement1))));
        // Update the element created
        String id = elementCreated.get("_id").get("$oid").asText();
        String updatedName = "new name";
        JsonNode changes = Json.newObject().put("name", updatedName);
        // Invoke the "Update" action using the Router
        Result result = route(new FakeRequest(PUT, "/telements/" + id).withJsonBody(changes));
        // Check response is OK
        Assert.assertEquals(OK, status(result));
        // Check Content-Type
        Assert.assertEquals("application/json", contentType(result));
        // Check Charset
        Assert.assertEquals("utf-8", charset(result));
        // Retrieve updated element
        JsonNode actual = Json.parse(contentAsString(route(new FakeRequest(GET, "/telements/" + id))));
        // Check if the modifications have been done correctly
        Assert.assertNotNull(actual.get("name"));
        Assert.assertEquals(updatedName, actual.get("name").asText());
        Assert.assertNotNull(actual.get("value"));
        Assert.assertEquals(elementCreated.get("value"), actual.get("value"));
      }
    });
  }

  @Test public void deleteTElementTest()
  {
    running(fakeApplication(), new Runnable()
    {
      public void run()
      {
        // Create an element
        JsonNode elementCreated = Json
          .parse(contentAsString(route(new FakeRequest(POST, "/telements").withJsonBody(tElement1))));
        String id = elementCreated.get("_id").get("$oid").asText();
        // Invoke the "Delete" action using the Router
        Result result = route(new FakeRequest(DELETE, "/telements/" + id));
        // Check response is OK
        Assert.assertEquals(OK, status(result));
        // Check that the element has been deleted by trying to find it by id
        Result result1 = route(new FakeRequest(GET, "/telements/" + id));
        Assert.assertEquals(NOT_FOUND, status(result1));
      }
    });
  }

  /**
   * HELPERS
   */

  // Helper method to remove all elements from the DB
  public void deleteAllTElements()
  {
    CrudController.mainService.deleteAllTElements();
  }
}
