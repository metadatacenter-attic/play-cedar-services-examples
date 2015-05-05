package org.metadatacenter.examples.crud;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.util.List;

public interface MainService<K, T>
{

  public T createTElement(T tElement) throws IOException;

  public List<T> findAllTElements() throws IOException;

  public T findTElementById(K tElementId) throws InstanceNotFoundException, IOException;

  public T updateTElement(K tElementId, T modifications) throws InstanceNotFoundException, IOException;

  public void deleteTElement(K tElementId) throws InstanceNotFoundException, IOException;

  public boolean existsTElement(K tElementId) throws IOException;

  public void deleteAllTElements();

}
