import com.fasterxml.jackson.databind.JsonNode;
import controllers.CrudController;
import org.junit.*;
import org.metadatacenter.examples.crud.MainService;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static play.test.Helpers.*;

/*
 * Integration Tests. They are done using a test server.
 */
public class CrudHttpTest {

    private final String serverUrl = "http://localhost:3333";
    private final int timeout = 10000;
    private static JsonNode tElement1;
    private static JsonNode tElement2;

    /**
     * One-time initialization code.
     * (Called once before any of the test methods in the class).
     */
    @BeforeClass
    public static void oneTimeSetUp() {
    }

    /**
     * (Called once after all the test methods in the class).
     */
    @AfterClass
    public static void oneTimeTearDown() {
    }

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public void setUp() {
        tElement1 = Json.newObject()
                .put("name", "element1 name")
                .put("value", "element1 value");
        tElement2 = Json.newObject()
                .put("name", "element2 name")
                .put("value", "element2 value");
        running(testServer(3333), new Runnable() {
            public void run() {
                deleteAllTElements();
            }
        });
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    @After
    public void tearDown() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Remove the elements created
                deleteAllTElements();
            }
        });
    }

    @Test
    public void createTElementTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Service invocation - Create
                WSResponse wsResponse = WS.url(serverUrl + "/telements").post(tElement1).get(timeout);
                // Check HTTP response
                Assert.assertEquals(OK, wsResponse.getStatus());
                // Check Content-Type
                Assert.assertEquals("application/json; charset=utf-8", wsResponse.getHeader("Content-Type"));
                String actualId = wsResponse.asJson().get("_id").get("$oid").asText();
                // Retrieve the element created
                JsonNode actual = WS.url(serverUrl + "/telements/" + actualId ).get().get(timeout).asJson();
                JsonNode expected = tElement1;
                // Check fields
                Assert.assertNotNull(actual.get("name"));
                Assert.assertEquals(expected.get("name"), actual.get("name"));
                Assert.assertNotNull(actual.get("value"));
                Assert.assertEquals(expected.get("value"), actual.get("value"));
            }
        });
    }

    @Test
    public void findAllTElementsTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Create two sample elements
                tElement1 = WS.url(serverUrl + "/telements").post(tElement1).get(timeout).asJson();
                tElement2 = WS.url(serverUrl + "/telements").post(tElement2).get(timeout).asJson();
                // Service invocation - Find all
                WSResponse wsResponse = WS.url(serverUrl + "/telements").get().get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check Content-Type
                Assert.assertEquals(wsResponse.getHeader("Content-Type"), "application/json; charset=utf-8");
                // Store actual and expected results into two sets, to compare them
                Set expectedSet = new HashSet<JsonNode>();
                expectedSet.add(tElement1);
                expectedSet.add(tElement2);
                Set actualSet = new HashSet<JsonNode>();
                JsonNode jsonResponse = wsResponse.asJson();
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

    @Test
    public void findTElementByIdTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Create an element
                JsonNode expected = WS.url(serverUrl + "/telements").post(tElement1).get(timeout).asJson();
                String id = expected.get("_id").get("$oid").asText();
                // Service invocation - Find by Id
                WSResponse wsResponse = WS.url(serverUrl + "/telements/" + id).get().get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check Content-Type
                Assert.assertEquals(wsResponse.getHeader("Content-Type"), "application/json; charset=utf-8");
                // Check the element retrieved
                JsonNode actual = wsResponse.asJson();
                Assert.assertEquals(expected, actual);
            }
        });
    }

    @Test
    public void updateTElementTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Create an element
                JsonNode elementCreated = WS.url(serverUrl + "/telements").post(tElement1).get(timeout).asJson();
                // Update the element created
                String id = elementCreated.get("_id").get("$oid").asText();
                String updatedName = "new name";
                JsonNode changes = Json.newObject().put("name", updatedName);
                // Service invocation - Update
                WSResponse wsResponse = WS.url(serverUrl + "/telements/" + id).put(changes).get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check Content-Type
                Assert.assertEquals(wsResponse.getHeader("Content-Type"), "application/json; charset=utf-8");
                // Retrieve updated element
                JsonNode actual = WS.url(serverUrl + "/telements/" + id).get().get(timeout).asJson();
                // Check if the modifications have been done correctly
                Assert.assertNotNull(actual.get("name"));
                Assert.assertEquals(updatedName, actual.get("name").asText());
                Assert.assertNotNull(actual.get("value"));
                Assert.assertEquals(elementCreated.get("value"), actual.get("value"));
            }
        });
    }

    @Test
    public void deleteTElementTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                // Create an element
                JsonNode elementCreated = WS.url(serverUrl + "/telements").post(tElement1).get(timeout).asJson();
                String id = elementCreated.get("_id").get("$oid").asText();
                // Service invocation - Delete
                WSResponse wsResponse = WS.url(serverUrl + "/telements/" + id).delete().get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check that the element has been deleted by trying to find it by id
                WSResponse wsResponse1 = WS.url(serverUrl + "/telements/" + id).get().get(timeout);
                Assert.assertEquals(NOT_FOUND, wsResponse1.getStatus());
            }
        });
    }

    // Helper method to remove all elements from the DB
    public void deleteAllTElements() {
        CrudController.mainService.deleteAllTElements();
    }
}
