package org.metadatacenter.examples.crud;

import com.fasterxml.jackson.databind.JsonNode;
import org.metadatacenter.examples.crud.telement.TElementDaoMongoDB;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.util.List;

public class MainServiceMongoDB implements MainService<String, JsonNode>
{

  private TElementDaoMongoDB tElementDao;

  public MainServiceMongoDB(String db, String tElementCollection)
  {
    tElementDao = new TElementDaoMongoDB(db, tElementCollection);
  }

  public JsonNode createTElement(JsonNode tElement) throws IOException
  {
    return tElementDao.create(tElement);
  }

  public List<JsonNode> findAllTElements() throws IOException
  {
    return tElementDao.findAll();
  }

  public JsonNode findTElementById(String tElementId) throws InstanceNotFoundException, IOException
  {
    return tElementDao.find(tElementId);
  }

  public JsonNode updateTElement(String tElementId, JsonNode modifications)
    throws InstanceNotFoundException, IOException
  {
    return tElementDao.update(tElementId, modifications);
  }

  public void deleteTElement(String tElementId) throws InstanceNotFoundException, IOException
  {
    tElementDao.delete(tElementId);
  }

  public boolean existsTElement(String tElementId) throws IOException
  {
    return tElementDao.exists(tElementId);
  }

  public void deleteAllTElements()
  {
    tElementDao.deleteAll();
  }

}
