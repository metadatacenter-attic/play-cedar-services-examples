import com.fasterxml.jackson.databind.JsonNode;
import controllers.CrudExampleController;
import org.junit.*;
import org.metadatacenter.examples.crud.MainService;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import javax.management.InstanceNotFoundException;
import java.io.IOException;

import static play.test.Helpers.*;

/*
 * Integration Tests. They are done using a test server.
 */
public class CrudExampleTest {

    private final String serverUrl = "http://localhost:3333";
    private final int timeout = 5000;
    private static JsonNode tElement1;
    private static JsonNode tElement2;
    private static MainService mainService;

    /**
     * One-time initialization code.
     * (Called once before any of the test methods in the class).
     */
    @BeforeClass
    public static void oneTimeSetUp() {
        tElement1 = Json.newObject()
                .put("name", "element1 name")
                .put("value", "element1 value");
        tElement2 = Json.newObject()
                .put("name", "element2 name")
                .put("value", "element2 value");
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
                JsonNode expected = tElement1;
                JsonNode actual = findTElementById(wsResponse.asJson().get("_id").get("$oid").asText());
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
                createTElement(tElement1);
                createTElement(tElement2);
                // Service invocation - Find all
                WSResponse wsResponse = WS.url(serverUrl + "/telements").get().get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check Content-Type
                Assert.assertEquals(wsResponse.getHeader("Content-Type"), "application/json; charset=utf-8");
                // Check the results obtained
                JsonNode jsonResponse = wsResponse.asJson();
                int size = jsonResponse.size();
                Assert.assertEquals(2, jsonResponse.size());
            }
        });
    }

    @Test
    public void findTElementByIdTest() {
        running(testServer(3333), new Runnable() {
            public void run() {
                JsonNode expected = createTElement(tElement1);
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
                JsonNode elementCreated = createTElement(tElement1);
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
                JsonNode actual = findTElementById(id);
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
                JsonNode elementCreated = createTElement(tElement1);
                String id = elementCreated.get("_id").get("$oid").asText();
                // Service invocation - Delete
                WSResponse wsResponse = WS.url(serverUrl + "/telements/" + id).delete().get(timeout);
                // Check response is OK
                Assert.assertEquals(wsResponse.getStatus(), OK);
                // Check that the element has been deleted
                WSResponse wsResponse1 = WS.url(serverUrl + "/telements/" + id).get().get(timeout);
                Assert.assertEquals(NOT_FOUND, wsResponse1.getStatus());
            }
        });
    }

    // Helpers
    public JsonNode findTElementById(String id) {
        JsonNode tElement = null;
        try {
            try {
                tElement = CrudExampleController.mainService.findTElementById(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        }
        return tElement;
    }

    public JsonNode createTElement(JsonNode tElement) {
        try {
            tElement = CrudExampleController.mainService.createTElement(tElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tElement;
    }

    public void deleteAllTElements() {
        CrudExampleController.mainService.deleteAllTElements();
    }
}
