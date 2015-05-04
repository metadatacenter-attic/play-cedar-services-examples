package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.metadatacenter.examples.crud.MainService;
import org.metadatacenter.examples.crud.MainServiceMongoDB;
import play.Configuration;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.management.InstanceNotFoundException;
import java.util.List;

public class CrudController extends Controller {

    public static MainService<String, JsonNode> mainService;

    static {
        Configuration config = Play.application().configuration();
        mainService = new MainServiceMongoDB(config.getString("mongodb.db"),
                config.getString("mongodb.collections.telements"));
    }

    public static Result createTElement() {
        try {
            JsonNode tElement = request().body().asJson();
            return ok(mainService.createTElement(tElement));
        } catch (Exception e) {
            return internalServerError();
        }
    }

    public static Result findAllTElements() {
        try {
            List<JsonNode> tElements = mainService.findAllTElements();

            return ok(Json.toJson(tElements));
        } catch (Exception e) {
            return internalServerError();
        }
    }

    public static Result findTElementById(String tElementId) {
        try {
            JsonNode tElement = mainService.findTElementById(tElementId);
            return ok(tElement);
        } catch (IllegalArgumentException e) {
            return badRequest();
        } catch (InstanceNotFoundException e) {
            return notFound();
        } catch (Exception e) {
            return internalServerError();
        }
    }

    public static Result updateTElement(String tElementId) {
        JsonNode modifications = request().body().asJson();
        try {
            JsonNode updatedTElement = mainService.updateTElement(tElementId, modifications);
            return ok(updatedTElement);
        } catch (IllegalArgumentException e) {
            return badRequest();
        } catch (InstanceNotFoundException e) {
            return notFound();
        } catch (Exception e) {
            return internalServerError();
        }
    }

    public static Result deleteTElement(String tElementId) {
        try {
            mainService.deleteTElement(tElementId);
            return ok();
        } catch (IllegalArgumentException e) {
            return badRequest();
        } catch (InstanceNotFoundException e) {
            return notFound();
        } catch (Exception e) {
            return internalServerError();
        }
    }
}
